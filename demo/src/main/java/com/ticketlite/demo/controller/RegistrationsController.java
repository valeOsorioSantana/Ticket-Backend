package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.RegistrationDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.service.RegistrationsService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@Tag(name = "Gestion de Registros", description = "Operaciones Crud para la administracion de los registros")
public class RegistrationsController {

    //Atributo
    private RegistrationsService registrationsService;

    //Constructor
    @Autowired
    public RegistrationsController(RegistrationsService registrationsService) {
        this.registrationsService = registrationsService;
    }

    //Metodos
    //GET BY ID
    //obtener registros por usuario
    @Operation(summary = "Buscar registros por usuario", description = "Busca un registro por usuario en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRegistrationsByUser(@PathVariable Long userId){
        try {
            List<RegistrationDTO> register = registrationsService.getRegistrationsDTOByUser(userId);
            return ResponseEntity.status(HttpStatus.OK).body(register);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }
    //obtener registros por evento
    @Operation(summary = "Buscar registros por evento", description = "Busca un registro por evento en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getRegistrationsByEvent(@PathVariable Long eventId){
        try {
            List<RegistrationsEntity> register = registrationsService.getRegistrationsByEvent(eventId);
            if (register == null || register.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron registros para el evento con ID: " + eventId);
            }

            return ResponseEntity.ok(register);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }

    //POST
    //Crear registro
    @Operation(summary = "Crear un registro", description = "Crea y guarda un nuevo registro en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente el registro",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos de registro invalidos"),
            @ApiResponse(responseCode = "500", description = "Error interno al crear el registro")
    })

    @PostMapping("/")
    public ResponseEntity<?> createRegistration(@RequestBody RegistrationsEntity registration) {
        try {
            if (registration.getUsers() == null || registration.getUsers().getId() == null) {
                return ResponseEntity.badRequest().body("El usuario es requerido");
            }
            if (registration.getEvents() == null || registration.getEvents().getId() == null) {
                return ResponseEntity.badRequest().body("El evento es requerido");
            }
            RegistrationsEntity result = registrationsService.register(registration);
/*
            RegistrationDTO dto = registrationsService.convertToDTO(result);
*/
            return ResponseEntity.status(HttpStatus.CREATED).body("Registro creado con ID: " + result.getId());
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno al crear el registro");
        }
    }

    //PUT
    @Operation(summary = "Actualizar un registro", description = "Actualiza y guarda un registro en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro actualizado con exito.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar el registro")
    })

    @PutMapping("/{registrationId}")
    public ResponseEntity<String>updateRegistrationDetails(@PathVariable Long registrationId,@RequestBody RegistrationsEntity registration){
        try {
            if (registration.getId() == null || !registration.getId().equals(registrationId)) {
                return ResponseEntity.badRequest().body("El ID del path no coincide con el del registro.");
            }
            RegistrationsEntity result = registrationsService.updateRegistrationDetails(registration);
            RegistrationDTO dto = registrationsService.convertToDTO(result);
            return ResponseEntity.ok("Registro actualizado con exito."+dto);
        }catch (NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al actualizar el registro");
        }
    }

    //DELETE
    @Operation(summary = "Eliminar un registro", description = "Elimina un registro en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro eliminado Correctamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
    })

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<String> deleteRegistration(@PathVariable Long registrationId){
        try {
            registrationsService.deleteRegistration(registrationId);
            return ResponseEntity.ok("Registro eliminado Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
