package com.ticketlite.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @Column(name = "first_name", length = 100, nullable = false)
    @Schema(description = "Nombre del usuario", example = "Juan", required = true)
    private String firstName;

    @Column(name = "last_name", length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Column(length = 20, nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema( description = "Rol del usuario en el sistema", example = "USUARIO", allowableValues = {"USUARIO", "ADMIN"}, required = true)
    private UserRole role = UserRole.USUARIO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Fecha y hora de creación del usuario", example = "2025-06-25T14:32:00")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Fecha y hora de última actualización", example = "2025-06-26T10:15:00")
    private LocalDateTime updatedAt;

    public enum UserRole {
        USUARIO,
        ADMIN
    }

    //Constructor

    public UsersEntity(Long id, String email, String passwordHash, String firstName, String lastName, String phone, String city, String bio, String avatarUrl, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.city = city;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public UserRole isRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
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
}
