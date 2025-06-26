package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.SessionsEntity;
import com.ticketlite.demo.service.SessionsService;
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

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Gestion de Sessiones", description = "Operaciones Crud para la administracion de las sessiones")
public class SessionsController {

    //Atributo
    private SessionsService sessionsService;

    //Constructor
    @Autowired
    public SessionsController(SessionsService sessionsService) {
        this.sessionsService = sessionsService;
    }

    //Metodos
    // Obtener una sesión por ID
    @Operation(summary = "Obtener una sesion por Id", description = "Recupera una session ya creada desde la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session encontrada por id exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SessionsEntity.class)))),
            @ApiResponse(responseCode = "404", description = "Session no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/{id}")
    public SessionsEntity getById(@PathVariable("id") Long sessionId) {
        return sessionsService.getById(sessionId);
    }

    // Obtener todas las sesiones de un evento por id (ordenadas por hora de inicio ascendente)
    @Operation(summary = "Obtener todas las sesiones de un evento por id", description = "Obtiene todas las sesiones de un evento por id (ordenadas por hora de inicio ascendente) en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessionsEntity.class))),
            @ApiResponse(responseCode = "204", description = "No hay sessiones disponibles")
    })

    @GetMapping("/event/{eventId}")
    public List<SessionsEntity> getBySessions(@PathVariable Long eventId) {
        return sessionsService.getBySessions(eventId);
    }

    //POST
    @Operation(summary = "Guardar una session", description = "Crea y guarda una nueva session en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente la session",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessionsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos de session invalidos")
    })
    //crear nueva session
    @PostMapping("/{eventId}")
    public ResponseEntity<?> saveSession (@PathVariable Long eventId, @RequestBody SessionsEntity sessionData){
        try {
            sessionsService.createSession(eventId, sessionData);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente la session");
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al crear la session");
        }
    }

    //PUT
    @Operation(summary = "Actualizar una session", description = "Actualiza y guarda una session en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizo correctamente la session" ,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessionsEntity.class))),
            @ApiResponse(responseCode = "404", description = "session no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar la session")
    })

    @PutMapping(value = "/sessions/{sessionId}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?>updateSession(@PathVariable Long sessionId, @RequestBody SessionsEntity updatedData){
        try {
            SessionsEntity updated = sessionsService.updateSession(sessionId, updatedData);
            return ResponseEntity.ok(updated);
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al actualizar la session");
        }
    }

    //DELETE
    @Operation(summary = "Eliminar una session", description = "Elimina una session en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se Elimino correctamente la session",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SessionsEntity.class))),
            @ApiResponse(responseCode = "404", description = "session no encontrada"),
    })
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable Long sessionId){
        try {
            sessionsService.deleteSession(sessionId);

            return ResponseEntity.ok("Session eliminada Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
