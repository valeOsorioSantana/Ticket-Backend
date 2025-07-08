package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.service.EventsService;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/events")
@Tag(name = "Gestion de Eventos", description = "Operaciones Crud para la administracion de eventos")
public class EventsController {
    //Atributo
    private EventsService eventsService;

    //Constructor
    @Autowired
    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    //Metodos
    //GET ALL
    @Operation(summary = "Obtener todos los eventos", description = "Recupera una lista de todos los eventos desde la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventsEntity.class)))),
            @ApiResponse(responseCode = "204", description = "No hay eventos disponibles")
    })

    @GetMapping("/")
    public List<EventsEntity> getAllEvents() {
        return eventsService.getAllEvents();
    }

    //Get for location
    @Operation(summary = "Buscar un evento cercano a la localizacion", description = "Busca un evento cercano a la localizacion actual del evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos cercanos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Eventos cercanos no encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/nearby")
    public List<EventsEntity> getEventsNearby(@RequestParam double lat, @RequestParam double lon, @RequestParam(defaultValue = "5000") double radius) {
        return eventsService.getEventsNearby(lat, lon, radius);
    }

    //GET UNIQUE GET BY ID
    @Operation(summary = "Buscar un evento por id", description = "Busca un evento por id en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEventById(@PathVariable Long eventId){
        try {
            Optional<EventsEntity> event = eventsService.getById(eventId);
            if (event.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(event.get());
            }else {
                return ResponseEntity.status(404).body("Evento no encontrado");
            }
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }

    //POST
    @Operation(summary = "Guardar un evento", description = "Crea y guarda un nuevo evento en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente el evento",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = EventsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos de eventos invalidos")
    })

    @PostMapping("/")
    public ResponseEntity<?> saveEvent(@RequestBody EventsEntity event){
        try {
            String result = eventsService.saveEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el evento");
        }catch (ConflictException e){
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    //PUT
    @Operation(summary = "Actualizar un evento", description = "Actualiza y guarda un evento en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizo correctamente el evento",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el evento")
    })

    @PutMapping("/{eventId}")
    public ResponseEntity<String>updateEvent(@PathVariable Long eventId, @RequestBody EventsEntity editEvent){
        try {
            editEvent.setId(eventId);
            EventsEntity updated = eventsService.updateEvent(eventId,editEvent);

            return ResponseEntity.ok("Evento actualizado con exito. ");
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al actualizar el evento");
        }
    }

    //DELETE
    @Operation(summary = "Eliminar un evento", description = "Elimina un evento en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se Elimino correctamente el evento",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id){
        try {
            eventsService.deleteEvent(id);

            return ResponseEntity.ok("Evento eliminado Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
