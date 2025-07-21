package com.ticketlite.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketlite.demo.DTO.EventCalendarDTO;
import com.ticketlite.demo.DTO.EventCompleteDTO;
import com.ticketlite.demo.DTO.EventDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.service.EventAnalyticsService;
import com.ticketlite.demo.service.EventsService;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@RestController
@RequestMapping("/api/public/events")
@Tag(name = "Gestión de Eventos", description = "Operaciones CRUD para la administración de eventos")
public class EventsController {

    private final EventsService eventsService;
    private final EventsRepository eventsRepository;
    private final GeometryFactory geometryFactory;
    private final ObjectMapper objectMapper;
    private final EventAnalyticsService eventAnalyticsService;

    @Autowired
    public EventsController(EventAnalyticsService eventAnalyticsService, EventsService eventsService, EventsRepository eventsRepository, GeometryFactory geometryFactory, ObjectMapper objectMapper) {
        this.eventsService = eventsService;
        this.eventsRepository = eventsRepository;
        this.geometryFactory = geometryFactory;
        this.objectMapper = objectMapper;
        this.eventAnalyticsService = eventAnalyticsService;
    }

    @GetMapping("/calendar")
    public List<EventCalendarDTO> getEventsForCalendar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<EventsEntity> events = (start != null && end != null)
                ? eventsRepository.findByStartDateBetween(start, end)
                : eventsRepository.findAll();

        return events.stream()
                .map(e -> new EventCalendarDTO(
                        e.getId(),
                        e.getName(),
                        e.getStartDate(),
                        e.getEndDate(),
                        e.getCategory(),
                        e.getAddress()
                ))
                .collect(toList());
    }

    @Operation(summary = "Obtener todos los eventos", description = "Recupera una lista de todos los eventos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = EventsEntity.class)))),
            @ApiResponse(responseCode = "204", description = "No hay eventos disponibles")
    })
    @GetMapping("/")
    public List<EventCompleteDTO> getAllEvents() {
        return eventsService.getAllEvents();
    }

    @Operation(summary = "Buscar eventos cercanos", description = "Busca eventos cercanos a una ubicación geográfica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos encontrados"),
            @ApiResponse(responseCode = "404", description = "No se encontraron eventos cercanos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/nearby")
    public List<EventCompleteDTO> getEventsNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "10000") double radius) {
        return eventsService.getEventsNearby(lat, lon, radius);
    }

    @GetMapping("/events/filter")
    public List<EventsEntity> filterEvents(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status
    ) {
        final EventsEntity.EventStatus statusEnum;
        try {
            statusEnum = (status != null) ? EventsEntity.EventStatus.valueOf(status.toUpperCase()) : null;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }

        return eventsRepository.findAll().stream()
                .filter(e -> startDate == null || !e.getStartDate().isBefore(startDate))
                .filter(e -> endDate == null || !e.getEndDate().isAfter(endDate))
                .filter(e -> category == null || e.getCategory().equalsIgnoreCase(category))
                .filter(e -> statusEnum == null || e.getStatus().equals(statusEnum))
                .collect(Collectors.toList());
    }


    @Operation(summary = "Obtener un evento por ID", description = "Busca un evento por su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId) {
        try {
            Optional<EventCompleteDTO> event = eventsService.getById(eventId);

            if (event.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            eventAnalyticsService.incrementView(eventId);
            return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al procesar la solicitud");
        }
    }

    @Operation(summary = "Guardar un evento", description = "Crea y guarda un nuevo evento con imagen adjunta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error al guardar evento"),
            @ApiResponse(responseCode = "409", description = "Datos de evento inválidos")
    })
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveEvent(
            @Parameter(
                    description = "Datos del evento en JSON (como String)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EventDTO.class))
            )
            @RequestPart("event") String eventJson,

            @Parameter(
                    description = "Archivo de imagen para el evento",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart("file") MultipartFile file) {

        try {
            EventDTO event = objectMapper.readValue(eventJson, EventDTO.class);
            eventsService.saveEvent(event, file);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el evento");
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al guardar evento: " + e.getMessage());
        }
    }

    @PostMapping("/{eventId}/rate")
    public ResponseEntity<?> rateEvent(@PathVariable Long eventId, @RequestParam("rating") BigDecimal rating) {

        if (rating == null || rating.compareTo(BigDecimal.ONE) < 0 || rating.compareTo(BigDecimal.valueOf(5)) > 0) {
            return ResponseEntity.badRequest().body("La calificación debe estar entre 1 y 5");
        }

        try {
            var updatedAnalytics = eventAnalyticsService.updateEsta(eventId, "satisfaction", rating);
            return ResponseEntity.ok(updatedAnalytics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar la calificación: " + e.getMessage());
        }
    }

    @Operation(summary = "Actualizar un evento", description = "Actualiza un evento existente y su imagen")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado con éxito"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto al actualizar evento"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PutMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateEvent(
            @PathVariable Long eventId,

            @Parameter(
                    description = "Datos del evento actualizado en JSON (como String)",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = EventDTO.class))
            )
            @RequestPart("event") String eventJson,

            @Parameter(
                    description = "Archivo de imagen actualizado",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            )
            @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            EventDTO editEvent = objectMapper.readValue(eventJson, EventDTO.class);
            editEvent.setId(eventId);
            eventsService.updateEvent(eventId, editEvent, file);
            return ResponseEntity.ok("Evento actualizado con éxito.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al actualizar el evento");
        }
    }

    @Operation(summary = "Eliminar un evento", description = "Elimina un evento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        try {
            eventsService.deleteEvent(id);
            return ResponseEntity.ok("Evento eliminado correctamente.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el archivo del evento");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}