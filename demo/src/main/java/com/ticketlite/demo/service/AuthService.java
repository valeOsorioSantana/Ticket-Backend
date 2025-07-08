package com.ticketlite.demo.service;

import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UsersRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final UsersService usersService;

    @Autowired
    public AuthService(UsersRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authManager, UsersService usersService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.usersService = usersService;
    }

    public String register(String username, String password, String name, String email) {
        // Validar que el email no esté registrado
        if (userRepo.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado: " + email);
        }

        if (name == null) {
            name = "";
        }

        UsersEntity user = new UsersEntity();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole("USER");

        userRepo.save(user);

        // Aquí asumimos que generateToken recibe String para "name"
        return "Usuario registrado con exito";
    }

    public String authenticate(String email, String rawPassword) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));

        UsersEntity existing = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String name = existing.getName().toString();

        return jwtService.generateToken(existing, name, email);
    }
}
