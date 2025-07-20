package com.ticketlite.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Schema(description = "Entidad que representa a un usuario dentro del sistema")
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "ID único del usuario", example = "1", required = true)
    private Long id;

    @Column(length = 255, nullable = false)
    @Schema(description = "Correo electrónico del usuario", example = "usuario@example.com", required = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    @Schema(description = "Hash de la contraseña del usuario", example = "$2a$10$abc123...", required = true)
    private String passwordHash;

    @NotBlank
    @Column(name = "name", length = 100, nullable = false)
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;


    @Column(name = "last_name", length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Column(length = 20)
    @Schema(description = "Teléfono del usuario", example = "+51987654321", required = true)
    private String phone;

    @Column(length = 100)
    @Schema(description = "Ciudad del usuario", example = "Lima")
    private String city;

    @Column(length = 255)
    @Schema(description = "Biografía o descripción del usuario", example = "Amante de la música y la tecnología.")
    private String bio;

    @Column(name = "avatar_url", length = 255)
    @Schema(description = "URL de la imagen de perfil del usuario", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Column(length = 255)
    @Schema(description = "El rol del usuario", example = "ADMIN-USUARIO")
    private String role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Fecha y hora de creación del usuario", example = "2025-06-25T14:32:00")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Fecha y hora de última actualización", example = "2025-06-26T10:15:00")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Schema(description = "Estado actual del usuario", example = "Activo", required = true,
            allowableValues = {"Inactivo"})
    private boolean estado;

    //Constructor


    public UsersEntity(boolean estado,Long id, String email, String passwordHash, String name, String lastName, String phone, String city, String bio, String avatarUrl, String role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.estado = estado;
    }

    public UsersEntity() {
    }

    //Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}