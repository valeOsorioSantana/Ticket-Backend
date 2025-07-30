package com.ticketlite.demo.service;


import com.ticketlite.demo.DTO.EventCompleteDTO;
import com.ticketlite.demo.DTO.EventDTO;
import com.ticketlite.demo.DTO.ImagenDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.Imagen;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ticketlite.demo.model.EventsEntity.EventStatus.FINALIZADO;

@Service
public class EventsService {

    //Atributos

    private RegistrationsService registrationsService;

    private EventsRepository eventsRepository;
    //Location
    private final GeometryFactory geometryFactory;

    private final ImageService imageService;

    private EventAnalyticsService eventAnalyticsService;

    //Importante para conectar el repository
    @Autowired
    public EventsService(EventAnalyticsService eventAnalyticsService, RegistrationsService registrationsService, GeometryFactory geometryFactory, ImageService imageService, EventsRepository eventsRepository) {
        this.geometryFactory = geometryFactory;
        this.imageService = imageService;
        this.eventsRepository = eventsRepository;
        this.registrationsService = registrationsService;
        this.eventAnalyticsService = eventAnalyticsService;
    }


    //Metodos
    //Get All
    public List<EventCompleteDTO> getAllEvents() {
        List<EventsEntity> events = eventsRepository.findAll();
        List<EventCompleteDTO> eventCompleteDTOS = new ArrayList<>();

        for (EventsEntity event : events) {
            Optional<Imagen> optionalImagen = imageService.getImage(event.getId(), "imagenes");
            Optional<Imagen> optionalImagenMapas = imageService.getImage(event.getId(), "mapas");

            Imagen imagen = optionalImagen.orElseGet(() -> {
                Imagen vacia = new Imagen();
                vacia.setKeyS3("");
                return vacia;
            });

            Imagen imagen1Map = optionalImagenMapas.orElseGet(() -> {
                Imagen vacia = new Imagen();
                vacia.setKeyS3("");
                return vacia;
            });

            EventCompleteDTO dto = convertDTO(event, Optional.of(imagen), Optional.of(imagen1Map));
            eventCompleteDTOS.add(dto);
        }

        return eventCompleteDTOS;
    }


    //Get Only
    public Optional<EventCompleteDTO> getById(Long eventId) {
        Optional<EventsEntity> event = eventsRepository.findById(eventId);

        if (event.isEmpty()) {
            return Optional.empty();
        }

        Optional<Imagen> imagen = imageService.getImage(eventId, "imagenes");

        Optional<Imagen> imagenMap = imageService.getImage(eventId, "mapas");

        EventCompleteDTO dto = convertDTO(event.get(), imagen, imagenMap);

        return Optional.of(dto);
    }

    //Get for location
    public List<EventCompleteDTO> getEventsNearby(double lat, double lon, double radius) {
        List<EventsEntity> nearbyEvents = eventsRepository.findEventsNearby(lat, lon, radius);
        List<EventCompleteDTO> eventDTOs = new ArrayList<>();

        for (EventsEntity event : nearbyEvents) {
            Optional<Imagen> imagen = imageService.getImage(event.getId(), "imagenes");

            Optional<Imagen> imagenMap = imageService.getImage(event.getId(), "mapas");

            EventCompleteDTO dto = convertDTO(event, imagen, imagenMap);
            eventDTOs.add(dto);
        }

        return eventDTOs;
    }

    //Filtrar eventos por categoria, ubicacion, estado y fecha
    public List<EventsEntity> filterEvents(LocalDateTime startDate, LocalDateTime endDate, String category, String status) {
        List<EventsEntity> result = eventsRepository.findAll();

        if (startDate != null) {
            result = result.stream()
                    .filter(e -> !e.getStartDate().isBefore(startDate))
                    .collect(Collectors.toList());
        }

        if (endDate != null) {
            result = result.stream()
                    .filter(e -> !e.getEndDate().isAfter(endDate))
                    .collect(Collectors.toList());
        }

        if (category != null) {
            result = result.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (status != null) {
            result = result.stream()
                    .filter(e -> status == null || e.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        return result;
    }

    //Post

    public EventsEntity saveEvent(EventDTO event, MultipartFile file, MultipartFile seatingMapFile) throws ConflictException {
        try {
            Optional<EventsEntity> existente = eventsRepository.findByName(event.getName());

            if (existente.isPresent()
                    && !existente.get().getId().equals(event.getId())
                    && !existente.get().getStatus().equals(FINALIZADO)) {

                throw new IllegalArgumentException("Ya existe un evento con ese nombre");
            }


            EventsEntity newEvent = new EventsEntity();

            if (event.getLatitude() != null && event.getLongitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point location = geometryFactory.createPoint(new Coordinate(event.getLongitude(), event.getLatitude()));
                location.setSRID(4326);
                newEvent.setLocation(location);
            } else {
                throw new RuntimeException("Latitud y longitud son obligatorios para crear la ubicación del evento.");
            }

            newEvent.setName(event.getName());
            newEvent.setDescription(event.getDescription());
            newEvent.setStartDate(event.getStartDate());
            newEvent.setEndDate(event.getEndDate());
            newEvent.setCategory(event.getCategory());
            newEvent.setStatus(event.getStatus());
            newEvent.setAddress(event.getAddress());
            newEvent.setTicketPrice(event.getTicketPrice());
            newEvent.setCreatedAt(LocalDateTime.now());

            // IMPORTANTE: guarda y asegura que tiene ID antes de asociar la imagen
            EventsEntity savedEvent = eventsRepository.saveAndFlush(newEvent);

            if (file != null && !file.isEmpty()) {
                imageService.uploadImage(file, savedEvent, "imagenes");
            }

            if (seatingMapFile != null && !seatingMapFile.isEmpty()) {
                imageService.uploadImage(seatingMapFile, savedEvent, "mapas");
            }

            return savedEvent;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el evento: " + e.getMessage(), e);
        }
    }

    //PUT
    @Transactional
    public EventsEntity updateEvent(Long eventId, EventDTO editEvent, MultipartFile file, MultipartFile seatingMapFile) throws NotFoundException, ConflictException {
        try {
            EventsEntity existingEvent = eventsRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Evento no encontrado con id " + eventId));

            boolean isSameName = existingEvent.getName().equals(editEvent.getName());
            boolean nameExists = !isSameName && eventsRepository.existsByName(editEvent.getName());

            if (nameExists) {
                throw new ConflictException("Ya existe un evento con ese nombre");
            }

            existingEvent.setName(editEvent.getName());
            existingEvent.setDescription(editEvent.getDescription());
            existingEvent.setStartDate(editEvent.getStartDate());
            existingEvent.setEndDate(editEvent.getEndDate());
            existingEvent.setCategory(editEvent.getCategory());
            existingEvent.setTicketPrice(editEvent.getTicketPrice());
            existingEvent.setStatus(editEvent.getStatus());
            existingEvent.setAddress(editEvent.getAddress());

            if (editEvent.getLatitude() != null && editEvent.getLongitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point newLocation = geometryFactory.createPoint(new Coordinate(editEvent.getLongitude(), editEvent.getLatitude()));
                newLocation.setSRID(4326);
                existingEvent.setLocation(newLocation);
            } else {
                throw new RuntimeException("Latitud y longitud son obligatorios para actualizar la ubicación del evento.");
            }

            EventsEntity updatedEvent = eventsRepository.saveAndFlush(existingEvent);

            if (file != null && !file.isEmpty()) {
                // Obtener la imagen asociada al evento (si existe)
                Optional<Imagen> optionalImagen = imageService.getImage(updatedEvent.getId(), "imagenes"); // Este método debe implementarse si no existe

                if (optionalImagen.isPresent()) {
                    imageService.updateImage(optionalImagen.get().getId(), file, "imagenes"); // Actualiza la imagen existente
                } else {
                    imageService.uploadImage(file, updatedEvent,"imagenes"); // Sube una nueva si no había
                }
            }

            if (seatingMapFile != null && !seatingMapFile.isEmpty()) {
                // Obtener la imagen asociada al evento (si existe)
                Optional<Imagen> optionalImagen = imageService.getImage(updatedEvent.getId(),"mapas"); // Este método debe implementarse si no existe

                if (optionalImagen.isPresent()) {
                    imageService.updateImage(optionalImagen.get().getId(), seatingMapFile, "mapas"); // Actualiza la imagen existente
                } else {
                    imageService.uploadImage(seatingMapFile, updatedEvent, "mapas"); // Sube una nueva si no había
                }
            }

            return updatedEvent;

        } catch (NotFoundException | ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el evento: " + e.getMessage(), e);
        }
    }


    //DELETE

    @Transactional
    public void deleteEvent(Long id) throws IOException {
        Logger log = LoggerFactory.getLogger(getClass());

        Optional<EventsEntity> event = eventsRepository.findById(id);

        // Intentar obtener la imagen asociada (opcional)
        Optional<Imagen> imagenOpt = imageService.getImage(id, "imagenes");

        Optional<Imagen> imagenMapOpt = imageService.getImage(id, "mapas");

        // Si la imagen existe, intentar eliminarla con manejo de errores
        try {
            imagenOpt.ifPresent(imagen -> {
                try {
                    imageService.deleteImage(imagen.getId());
                    log.info("Imagen asociada al evento {} eliminada correctamente.", id);
                } catch (Exception e) {
                    log.warn("No se pudo eliminar la imagen asociada al evento {}: {}", id, e.getMessage());
                }
            });
        } catch (Exception ex) {
            log.warn("Error inesperado al procesar imagen asociada al evento {}: {}", id, ex.getMessage());
        }

        try {
            imagenMapOpt.ifPresent(imagen -> {
                try {
                    imageService.deleteImage(imagen.getId());
                    log.info("Imagen asociada al evento {} eliminada correctamente.", id);
                } catch (Exception e) {
                    log.warn("No se pudo eliminar la imagen asociada al evento {}: {}", id, e.getMessage());
                }
            });
        } catch (Exception ex) {
            log.warn("Error inesperado al procesar imagen asociada al evento {}: {}", id, ex.getMessage());
        }

        event.get().setStatus(FINALIZADO);

    }


    private EventCompleteDTO convertDTO(EventsEntity eventsEntity, Optional<Imagen> imagenOpt, Optional<Imagen> imagenMapOpt) {
        EventCompleteDTO dto = new EventCompleteDTO();

        dto.setId(eventsEntity.getId());
        dto.setName(eventsEntity.getName());
        dto.setDescription(eventsEntity.getDescription());
        dto.setStartDate(eventsEntity.getStartDate());
        dto.setEndDate(eventsEntity.getEndDate());

        if (eventsEntity.getLocation() != null) {
            dto.setLatitude(eventsEntity.getLocation().getY());
            dto.setLongitude(eventsEntity.getLocation().getX());
        }

        dto.setCategory(eventsEntity.getCategory());
        dto.setTicketPrice(eventsEntity.getTicketPrice());
        dto.setStatus(eventsEntity.getStatus());
        dto.setAddress(eventsEntity.getAddress());
        dto.setCreatedAt(eventsEntity.getCreatedAt());


        List<ImagenDTO> imagenesDto = new ArrayList<>();

        // Convertir Imagen a ImagenDTO con URL prefirmada
        if (imagenOpt.isPresent()) {
            Imagen imagen = imagenOpt.get();
            try {
                String url = imageService.getPresignedUrl(imagen.getId());
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), url);
                imagenesDto.add(imagenDTO);
            } catch (Exception e) {
                // En caso de error al generar URL
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), "Error al generar URL");
                imagenesDto.add(imagenDTO);
            }
        }

        // Convertir Imagen a ImagenDTO con URL prefirmada
        if (imagenMapOpt.isPresent()) {
            Imagen imagen = imagenMapOpt.get();
            try {
                String url = imageService.getPresignedUrl(imagen.getId());
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), url);
                imagenesDto.add(imagenDTO);
            } catch (Exception e) {
                // En caso de error al generar URL
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), "Error al generar URL");
                imagenesDto.add(imagenDTO);
            }
        }

        dto.setImagenes(imagenesDto);

        return dto;
    }
}