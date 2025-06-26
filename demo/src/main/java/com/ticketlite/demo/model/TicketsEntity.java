package com.ticketlite.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class TicketsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único del ticket", example = "1", required = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "registration_id", nullable = false)
    @Schema(description = "Registro asociado al ticket", required = true)
    private RegistrationsEntity registration;

    @CreationTimestamp
    @Column(name = "issued_at", nullable = false)
    @Schema(description = "Fecha y hora en que se emitió el ticket", example = "2025-06-18T14:30:00", required = true)
    private LocalDateTime issuedAt;

    @Column(name = "qr_code", length = 255)
    @Schema(description = "Código QR asociado al ticket", example = "QR123456789")
    private String qrCode;

    // Constructor
    public TicketsEntity(Long id, RegistrationsEntity registration, LocalDateTime issuedAt, String qrCode) {
        this.id = id;
        this.registration = registration;
        this.issuedAt = issuedAt;
        this.qrCode = qrCode;
    }

    public TicketsEntity() {}

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistrationsEntity getRegistration() {
        return registration;
    }

    public void setRegistration(RegistrationsEntity registration) {
        this.registration = registration;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
