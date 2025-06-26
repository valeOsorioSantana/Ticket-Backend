package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.PaymentsEntity;
import com.ticketlite.demo.service.PaymentsService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Gestion de Compras", description = "Operaciones Crud para la administracion de las compras")
public class PaymentsController {

    //Atributos
    private PaymentsService paymentsService;

    //Constructor
    @Autowired
    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    //Metodos
    //GET BY ID
    //obtener pago por id
    @Operation(summary = "Buscar un pago por id", description = "Busca un pago por id en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long paymentId){
        try {
            PaymentsEntity payment = paymentsService.getPaymentById(paymentId);
            return ResponseEntity.status(HttpStatus.OK).body(payment);
        }catch (Exception e){
            return ResponseEntity.status(500).body("Error interno al procesar la solicitud");
        }
    }
    //obtener pago por id de registro (unico)
    @Operation(summary = "Buscar un pago por id de registro", description = "Busca un pago por id de registro (unico) en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud")
    })

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<?> getPaymentByRegistration(@PathVariable Long registrationId) {
        try {
            Optional<PaymentsEntity> payment = paymentsService.getPaymentByRegistration(registrationId);
            if (payment.isPresent()) {
                return ResponseEntity.ok(payment.get());
            } else {
                return ResponseEntity.status(404).body("No se encontró un pago para el registro ID: " + registrationId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar el pago: " + e.getMessage());
        }
    }

    //POST
    //crear un nuevo pago
    @Operation(summary = "Guardar un pago", description = "Crea y guarda un nuevo pago en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Se creó exitosamente el pago",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentsEntity.class))),
            @ApiResponse(responseCode = "409", description = "Datos del pago invalidos"),
            @ApiResponse(responseCode = "500", description = "Error al crear el pago")
    })

    @PostMapping("/")
    public ResponseEntity<?> createPayment(@RequestParam Long registrationId, @RequestParam String method, @RequestParam BigDecimal amount ){
        try {
            PaymentsEntity created = paymentsService.createPayment(registrationId, method, amount);
            return ResponseEntity.status(HttpStatus.CREATED).body("Se creó exitosamente el pago"+ created);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear el pago: " + e.getMessage());
        }
    }

    //PUT
    //actualizar estado del pago
    @Operation(summary = "Actualizar el estado del pago", description = "Actualiza y guarda el estado del pago en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizo correctamente el estado del pago",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error al actualizar el estado del pago")
    })

    @PutMapping("/{paymentId}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long paymentId, @RequestParam PaymentsEntity.PaymentStatus status) {
        try {
            PaymentsEntity updated = paymentsService.updatePaymentStatus(paymentId, status);
            return ResponseEntity.ok("Se actualizo exitosamente el pago"+ updated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el estado del pago: " + e.getMessage());
        }
    }

    //DELETE
    //eliminar pago
    @Operation(summary = "Eliminar un pago", description = "Elimina un pago en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se Elimino correctamente el pago",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentsEntity.class))),
            @ApiResponse(responseCode = "404", description = "Error al eliminar el pago"),
    })
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable Long paymentId) {
        try {
            paymentsService.deletePay(paymentId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error al eliminar el pago: " + e.getMessage());
        }
    }

}
