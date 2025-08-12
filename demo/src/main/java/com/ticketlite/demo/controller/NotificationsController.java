package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.NotiRespuestaDTO;
import com.ticketlite.demo.model.NotificationsEntity;
import com.ticketlite.demo.service.NotificationsService;
import com.ticketlite.demo.structure.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/notifications")
@Tag(name = "Gestion de Notificaciones", description = "Operaciones Crud para la administracion de las notificaciones")
public class NotificationsController {

    //Atributos
    private NotificationsService notificationsService;

    //Constructor
    @Autowired
    public NotificationsController(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    //Metodos
    //GET ALL NOTIFICATIONS BY ID USER
    @Operation(summary = "Buscar una notificacion por id del user", description = "Busca una notificacion por id del user en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificacion encontrada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getNotificationsByUser(@PathVariable Long userId) {
        try {
            List<NotificationsEntity> notifications = notificationsService.getNotificationsByUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener notificaciones del usuario: " + e.getMessage());
        }
    }

    //POST
    //crear una nueva notificacion
    @Operation(summary = "Guardar una notificacion", description = "Crea y guarda una nueva notificacion en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente la notificacion",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos invalidos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear la notificacion")
    })

    @PostMapping("/responder")
    public ResponseEntity<?> responderNotificacion(@RequestBody NotiRespuestaDTO request) {
        try {
            // Aquí solo se simula la respuesta, puedes expandirla para guardarla si deseas.
            if (request.getNotificacionId() == null || request.getMensaje() == null || request.getMensaje().isBlank()) {
                return ResponseEntity.badRequest().body("Datos incompletos para responder.");
            }

            // Puedes loguear o procesar la respuesta como desees.
            System.out.println("Respuesta enviada a notificación ID " + request.getNotificacionId() + ": " + request.getMensaje());

            return ResponseEntity.ok("Respuesta enviada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al responder la notificación: " + e.getMessage());
        }
    }

    //PUT
    //marcar una notificacion como leida
    @Operation(summary = "Marcar una notificacion como leida", description = "Marca una notificacion como leida en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno al marcar la notificacion como leida")
    })
    @PutMapping("/{notificationId}/mark-as-read")
    public ResponseEntity<?>markRead(@PathVariable Long notificationId){
        try {
            NotificationsEntity notifications = notificationsService.markRead(notificationId);

            return ResponseEntity.ok("Notificacion marcada como leida.");
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al marcar notificaciones como leidas");
        }
    }

    //PUT
    //marcar notificaciones como leidas
    @Operation(summary = "Marcar notificaciones como leidas", description = "Marca notificaciones como leidas en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones marcadas como leidas.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "No hay notificaciones no leídas"),
            @ApiResponse(responseCode = "500", description = "Error interno al marcar notificaciones como leidas")
    })
    @PutMapping("/{userId}/mark-all-as-read")
    public ResponseEntity<?>markAllNotiRead(@PathVariable Long userId){
        try {
            notificationsService.markAllNotiRead(userId);
            return ResponseEntity.ok("Notificaciones marcadas como leidas.");
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al marcar notificaciones como leidas");
        }
    }

    //DELETE
    //eliminar una notificacion
    @Operation(summary = "Eliminar una notificacion", description = "Elimina una notificacion en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificacion eliminada Correctamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "notificacion no encontrada"),
    })

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNoti(@PathVariable("notificationId") Long notificationId){
        try {
            notificationsService.deleteNoti(notificationId);

            return ResponseEntity.ok("Notificacion eliminada Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
