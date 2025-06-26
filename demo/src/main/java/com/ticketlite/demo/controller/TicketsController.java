package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.TicketsEntity;
import com.ticketlite.demo.service.TicketsService;
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
@RequestMapping("/api/tickets")
@Tag(name = "Gestion de Tickets", description = "Operaciones Crud para la administracion de los tickets")
public class TicketsController {

    //Atributos
    public TicketsService ticketsService;

    //Constructor
    @Autowired
    public TicketsController(TicketsService ticketsService) {
        this.ticketsService = ticketsService;
    }

    //Metodos
    //GET ALL
    @Operation(summary = "Obtener todos los tickets", description = "Recupera una lista de todos los tickets desde la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tickets obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TicketsEntity.class)))),
            @ApiResponse(responseCode = "204", description = "No hay tickets disponibles")
    })

    @GetMapping("/")
    public List<TicketsEntity> getAllTickets(){
        return ticketsService.getAllTickets();
    }

    //GET BY ID
    //obtener ticket por ID
    @Operation(summary = "Obtener un ticket por id", description = "Obtiene un ticket por id en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable("id") Long ticketId){
        try {
            TicketsEntity ticket = ticketsService.getTicketById(ticketId);
            return ResponseEntity.ok(ticket);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }
    //obtener ticket por ID de registro
    @Operation(summary = "Obtiene ticket por ID de registro", description = "obtiene ticket por ID de registro en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ticket encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Ticket no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/resgistration/{registrationId}")
    public ResponseEntity<?> getTicketByRegistration(@PathVariable Long registrationId){
        try {
            TicketsEntity ticket = ticketsService.getTicketByRegistration(registrationId);
            return ResponseEntity.ok(ticket);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }

    //POST
    @Operation(summary = "Guardar un ticket", description = "Crea y guarda un nuevo ticket en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente el ticket",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Qr del ticket invalido"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")

    })
    //crear un ticket
    @PostMapping("/{registrationId}")
    public ResponseEntity<?> createTicket(@PathVariable Long registrationId, @RequestParam String qrCode){
        try {
            TicketsEntity result = ticketsService.createTicket(registrationId, qrCode);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el evento");
        }catch(NotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al crear el ticket");
        }
    }

    //DELETE
    @Operation(summary = "Eliminar un ticket", description = "Elimina un ticket en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se Elimino correctamente el ticket",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TicketsEntity.class))),
            @ApiResponse(responseCode = "404", description = "ticket no encontrado"),
    })

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable Long ticketId){
        try {
            ticketsService.deleteTicket(ticketId);
            return ResponseEntity.ok("Evento eliminado Correctamente.");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
