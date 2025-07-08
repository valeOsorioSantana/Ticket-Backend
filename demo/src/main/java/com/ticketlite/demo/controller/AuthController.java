package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.security.JwtResponse;
import com.ticketlite.demo.security.JwtService;
import com.ticketlite.demo.security.LoginRequest;
import com.ticketlite.demo.security.RegisterRequestDTO;
import com.ticketlite.demo.service.AuthService;
import com.ticketlite.demo.service.UsersService;
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDTO request) {
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



    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // Validar usuario
        Optional<UsersEntity> userAu = userService.authenticateUser(email, password);
        if (userAu.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Correo electrónico o contraseña incorrectos.");
        }
        UsersEntity user = userAu.get();

        String name = user.getName().toString();
        // Generar token JWT
        String token = jwtService.generateToken(user, name, email);

        return ResponseEntity.ok(new JwtResponse(token));
    }
}
