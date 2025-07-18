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

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventsService {

    //Atributos

    private EventsRepository eventsRepository;
    //Location
    private final GeometryFactory geometryFactory;

    private final ImageService imageService;

    //Importante para conectar el repository
    @Autowired
    public EventsService(GeometryFactory geometryFactory, ImageService imageService, EventsRepository eventsRepository) {
        this.geometryFactory = geometryFactory;
        this.imageService = imageService;
        this.eventsRepository = eventsRepository;
    }


    //Metodos
    //Get All
    public List<EventCompleteDTO> getAllEvents() {
        List<EventsEntity> events = eventsRepository.findAll();
        List<EventCompleteDTO> eventCompleteDTOS = new ArrayList<>();

        for (EventsEntity event : events) {
            Optional<Imagen> imagene = imageService.getImage(event.getId());
            EventCompleteDTO dto = convertDTO(event, imagene);
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

        Optional<Imagen> imagen = imageService.getImage(eventId);
        EventCompleteDTO dto = convertDTO(event.get(), imagen);

        return Optional.of(dto);
    }

    //Get for location
    public List<EventCompleteDTO> getEventsNearby(double lat, double lon, double radius) {
        List<EventsEntity> nearbyEvents = eventsRepository.findEventsNearby(lat, lon, radius);
        List<EventCompleteDTO> eventDTOs = new ArrayList<>();

        for (EventsEntity event : nearbyEvents) {
            Optional<Imagen> imagen = imageService.getImage(event.getId());
            EventCompleteDTO dto = convertDTO(event, imagen);
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

    public EventsEntity saveEvent(EventDTO event, MultipartFile file) throws ConflictException {
        try {
            if (eventsRepository.existsByName(event.getName())) {
                throw new ConflictException("Ya existe un evento con ese nombre");
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
            newEvent.setCreatedAt(LocalDateTime.now());

            // IMPORTANTE: guarda y asegura que tiene ID antes de asociar la imagen
            EventsEntity savedEvent = eventsRepository.saveAndFlush(newEvent);

            imageService.uploadImage(file, savedEvent);

            return savedEvent;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el evento: " + e.getMessage(), e);
        }
    }

    //PUT
    @Transactional
    public EventsEntity updateEvent(Long eventId, EventDTO editEvent, MultipartFile file) throws NotFoundException, ConflictException {
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
                Optional<Imagen> optionalImagen = imageService.getImage(updatedEvent.getId()); // Este método debe implementarse si no existe

                if (optionalImagen.isPresent()) {
                    imageService.updateImage(optionalImagen.get().getId(), file); // Actualiza la imagen existente
                } else {
                    imageService.uploadImage(file, updatedEvent); // Sube una nueva si no había
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
        if (!eventsRepository.existsById(id)) {
            throw new NotFoundException("Evento no encontrado por ID: " + id);
        }

        // Intentar obtener la imagen asociada (opcional)
        Optional<Imagen> imagenOpt = imageService.getImage(id);

        // Si la imagen existe, eliminarla
        if (imagenOpt.isPresent()) {
            imageService.deleteImage(imagenOpt.get().getId());
        }

        // Finalmente eliminar el evento
        eventsRepository.deleteById(id);
    }



    private EventCompleteDTO convertDTO(EventsEntity eventsEntity, Optional<Imagen> imagenOpt) {
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
        dto.setStatus(eventsEntity.getStatus());
        dto.setAddress(eventsEntity.getAddress());
        dto.setCreatedAt(eventsEntity.getCreatedAt());

        // Convertir Imagen a ImagenDTO con URL prefirmada
        if (imagenOpt.isPresent()) {
            Imagen imagen = imagenOpt.get();
            try {
                String url = imageService.getPresignedUrl(imagen.getId());
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), url);
                dto.setImagen(imagenDTO);
            } catch (Exception e) {
                // En caso de error al generar URL
                ImagenDTO imagenDTO = new ImagenDTO(imagen.getId(), imagen.getNombreOriginal(), "Error al generar URL");
                dto.setImagen(imagenDTO);
            }
        }

        return dto;
    }


}