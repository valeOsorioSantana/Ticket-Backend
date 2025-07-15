package com.ticketlite.demo.service;

import com.ticketlite.demo.model.PasswordTokenEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.PasswordTokenCrudRepository;
import com.ticketlite.demo.model.repository.UsersRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Service
public class PasswordRecoveryServiceImpl{

    private PasswordTokenCrudRepository tokenRepo;
    private EmailServiceImple emailService;
    private PasswordEncoder passwordEncoder;
    private UsersRepository usuarioRepository;

    @Autowired
    public PasswordRecoveryServiceImpl(PasswordTokenCrudRepository tokenRepo, EmailServiceImple emailService, PasswordEncoder passwordEncoder, UsersRepository usuarioRepository) {
        this.tokenRepo = tokenRepo;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    public void iniciarRecuperacion(String email) throws MessagingException {
        // Verifica si el usuario existe por email
        Optional<UsersEntity> usuarioOpt = usuarioRepository.getByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("El correo no está registrado en la aplicación");
        }

        UsersEntity usuario = usuarioOpt.get();

        // Genera el token

        String token = generateToken();

        PasswordTokenEntity tokenEntity = new PasswordTokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setEmail(email);
        tokenEntity.setExpiration(LocalDateTime.now().plusMinutes(15));
        tokenEntity.setUsed(false);

        // Guarda el token
        tokenRepo.save(tokenEntity);

        // Prepara la URL de recuperación con el token como parámetro
        String url = /*"http://localhost:8080/api/recuperacion/cambiar?token=" +*/ token;

        // Envia el correo con el nombre del usuario y la URL personalizada
        emailService.enviarCorreoRecuperacion(email, usuario.getName() + " " +
                usuario.getLastName(), url);
    }

    public boolean validarToken(String token) {
        return tokenRepo.findByToken(token)
                .filter(t -> !t.isUsed() && t.getExpiration().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void cambiarPassword(String token, String nuevaPassword) {
        PasswordTokenEntity tokenEntity = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (tokenEntity.isUsed() || tokenEntity.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado o ya usado");
        }

        // aquí deberías buscar al usuario por email y cambiarle la contraseña
        UsersEntity usuario = usuarioRepository.getByEmail(tokenEntity.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        tokenEntity.setUsed(true);
        tokenRepo.save(tokenEntity);
    }

    // Ejecuta cada hora
    @Scheduled(cron = "0 0 * * * *") // (segundo, minuto, hora, día, mes, día_semana)
    public void eliminarTokensExpirados() {
        tokenRepo.deleteByExpirationBefore(LocalDateTime.now());
        System.out.println("Se eliminaron tokens expirados: " + LocalDateTime.now());
    }

    private String generateToken() {
        Random random = new Random();
        int parte1 = 100 + random.nextInt(900); // número entre 100 y 999
        int parte2 = 100 + random.nextInt(900); // número entre 100 y 999
        return parte1 + "-" + parte2;
    }
}