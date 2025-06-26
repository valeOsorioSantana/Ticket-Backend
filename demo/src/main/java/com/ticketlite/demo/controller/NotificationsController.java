package com.ticketlite.demo.controller;

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

    @PostMapping("/")
    public ResponseEntity<?> crearNotifi(@RequestParam Long userId, @RequestParam(required = false) Long eventId, @RequestParam String message, @RequestParam String type){
        try {
            NotificationsEntity result = notificationsService.crearNotifi(userId, eventId, message, type);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente la notificacion"+ result);
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al crear la notificacion");
        }
    }

    //PUT
    //marcar notificaciones como leidas
    @Operation(summary = "Marcar notificaciones como leidas", description = "Marca notificaciones como leidas en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Las notificaciones han sido marcadas como leidas.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = NotificationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Notificacion no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno al marcar notificaciones como leidas")
    })

    @PutMapping("/{id}")
    public ResponseEntity<String>markRead(@PathVariable Long notificationId, @RequestBody NotificationsEntity notifications){
        try {
            notifications.setId(notificationId);
            notificationsService.markRead(notificationId);
            return ResponseEntity.ok("Las notificaciones han sido marcadas como leidas.");
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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNoti(@PathVariable Long notificationId){
        try {
            notificationsService.deleteNoti(notificationId);

            return ResponseEntity.ok("Notificacion eliminada Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

}
