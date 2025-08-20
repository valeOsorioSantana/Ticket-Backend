package com.ticketlite.demo.controller;

import com.ticketlite.demo.service.PasswordRecoveryServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recuperacion")
@Tag(name = "Recuperación de Contraseña", description = "Operaciones para la recuperación y cambio de contraseña de usuarios")
public class PasswordRecoveryController {

    private PasswordRecoveryServiceImpl recoveryService;

    @Autowired
    public PasswordRecoveryController(PasswordRecoveryServiceImpl recoveryService) {
        this.recoveryService = recoveryService;
    }

    //Ejemplo petición:
    //http://localhost:8080/api/recuperacion/solicitar?email=hhmjhonatan@gmail.com
    @Operation(summary = "Solicitar token de recuperación",
            description = "Envía un correo electrónico al usuario con un token para recuperar la contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo de recuperación enviado con éxito"),
            @ApiResponse(responseCode = "400", description = "Email inválido o no registrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al enviar el correo")
    })
    @PostMapping("/solicitar")
    public ResponseEntity<?> solicitarToken(@Parameter(description = "Email del usuario que solicita la recuperación", required = true) @RequestParam String email) throws MessagingException {
        recoveryService.iniciarRecuperacion(email) ;
        return ResponseEntity.ok("Correo enviado");
    }

    //Ejemplo petición:
    //http://localhost:8080/api/recuperacion/cambiar?token=4ac9b692-c417-4c8d-a574-410e4f5cc932
    @Operation(summary = "Validar token de recuperación",
            description = "Verifica si un token de recuperación es válido o ha expirado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna el estado del token (válido o inválido)"),
            @ApiResponse(responseCode = "400", description = "Token inválido o mal formado")
    })
    @GetMapping("/validar")
    public ResponseEntity<?> validarToken(@Parameter(description = "Token de recuperación enviado por correo", required = true) @RequestParam String token) {
        boolean valido = recoveryService.validarToken(token);
        return ResponseEntity.ok(valido ? "Token válido" : "Token inválido o expirado");
    }

    //Ejemplo petición:
    //http://localhost:8080/api/recuperacion/cambiar?token=7df4e273-6961-4a28-aff5-645349c8cced&nuevaPassword=jhonatan123
    @Operation(summary = "Cambiar contraseña",
            description = "Permite actualizar la contraseña del usuario usando un token válido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar la contraseña")
    })
    @PostMapping("/cambiar")
    public ResponseEntity<?> cambiarPassword(@Parameter(description = "Token de recuperación válido", required = true) @RequestParam String token, @Parameter(description = "Nueva contraseña del usuario", required = true) @RequestParam String nuevaPassword) {
        recoveryService.cambiarPassword(token, nuevaPassword);
        return ResponseEntity.ok("Contraseña actualizada");
    }
}