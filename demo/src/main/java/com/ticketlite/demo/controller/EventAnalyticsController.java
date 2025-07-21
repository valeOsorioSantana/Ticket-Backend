package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventAnalyticsEntity;
import com.ticketlite.demo.service.EventAnalyticsService;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/analisis")
@Tag(name = "Gestion de Estadisticas", description = "Operaciones Crud para la administracion de las estadisticas de los eventos")
public class EventAnalyticsController {

    //Atributos
    private EventAnalyticsService eventAnalyticsService;

    //Constructor
    @Autowired
    public EventAnalyticsController(EventAnalyticsService eventAnalyticsService) {
        this.eventAnalyticsService = eventAnalyticsService;
    }

    //Metodos
    //GET BY ID EVENT
    @Operation(summary = "Buscar analisis por id de evento", description = "Busca las estadisticas por id del evento en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadisticas de Evento encontradas exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventAnalyticsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping(value = "/event/{eventId}", produces = "application/json")
    public ResponseEntity<?> getByIdEvent(@PathVariable("eventId") Long eventId) {
        try {
            return eventAnalyticsService.getByIdEvent(eventId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }

    //PUT
    //actualizar y crear estadisticas
    @Operation(summary = "Actualizar una estadistica", description = "Actualiza y guarda una estadistica en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizo correctamente la estadistica",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EventAnalyticsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Estadistica no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar la estadistica")
    })

    @PutMapping("/{eventId}")
    public ResponseEntity<?> updateEsta(@Parameter(description = "ID del análisis a actualizar", required = true) @PathVariable Long eventId, @Parameter(description = "Acción a realizar: view, registration, tickets, satisfaction", required = true) @RequestParam String action, @Parameter(description = "Valor para la acción, solo requerido para tickets y satisfaction", required = false) @RequestParam(required = false) BigDecimal value) {
        try {

            EventAnalyticsEntity updated = eventAnalyticsService.updateEsta(eventId, action, value);

            return ResponseEntity.ok("Estadistica actualizada con exito."+ updated);
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al actualizar la estadistica");
        }
    }
}
