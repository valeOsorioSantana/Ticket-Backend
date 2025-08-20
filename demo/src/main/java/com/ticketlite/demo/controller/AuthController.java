package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.security.JwtResponse;
import com.ticketlite.demo.security.JwtService;
import com.ticketlite.demo.security.LoginRequest;
import com.ticketlite.demo.security.RegisterRequestDTO;
import com.ticketlite.demo.service.AuthService;
import com.ticketlite.demo.service.UsersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Operaciones para registro y login de usuarios")
public class AuthController {

    private final AuthService authService;

    private UsersService userService;

    private JwtService jwtService;

    private UsersRepository usersRepository;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthService authService, UsersService userService, JwtService jwtService, UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Registrar un nuevo usuario",
            description = "Permite registrar un nuevo usuario. Retorna un mensaje indicando que el registro fue exitoso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del usuario a registrar",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequestDTO.class)))@RequestBody RegisterRequestDTO request) {
        // Aquí conviertes de strings a Roles dentro de tu servicio, ejemplo:
        String token = authService.register(
                request.getUsername(),
                request.getPassword(),
                request.getName(),
                request.getEmail()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User register success!"));
    }

    @Operation(
            summary = "Autenticar un usuario",
            description = "Realiza el login de un usuario existente y devuelve un token JWT si las credenciales son correctas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario autenticado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"token\":\"jwt-token...\"}"))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"error\":\"Credenciales incorrectas\"}")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales del usuario",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class)))@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<String> tokenOptional = authService.authenticate(email, password);

        if (tokenOptional.isPresent()) {
            String token = tokenOptional.get();
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }
    }
}
