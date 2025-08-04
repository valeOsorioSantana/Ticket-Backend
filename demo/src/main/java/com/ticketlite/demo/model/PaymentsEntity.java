package com.ticketlite.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Schema(description = "Modelo que representa un pago realizado por una inscripción a un evento")
public class PaymentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único del pago", example = "1", required = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    @JsonBackReference
    @Schema(description = "Registro asociado al pago")
    private RegistrationsEntity registration;

    @Column(length = 100)
    @Schema(description = "Método de pago utilizado", example = "Tarjeta de crédito")
    private String method;

    @Column(precision = 10, scale = 2)
    @Schema(description = "Monto total del pago", example = "59.99")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(
            description = "Estado actual del pago",
            example = "PAGADO",
            allowableValues = {"PENDIENTE", "PAGADO", "FALLIDO", "REEMBOLSADO"},
            required = true
    )
    private PaymentStatus status = PaymentStatus.PENDIENTE;

    @CreationTimestamp
    @Column(name = "paid_at", nullable = false, updatable = false)
    @Schema(description = "Fecha y hora en que se registró el pago", example = "2025-06-18T14:45:00")
    private LocalDateTime paidAt;

    @Schema(description = "Enumeración que representa los estados posibles del pago")
    public enum PaymentStatus {
        PENDIENTE,
        PAGADO,
        CANCELADO,
        REEMBOLSADO
    }

    // Constructor
    public PaymentsEntity(Long id, RegistrationsEntity registration, String method, BigDecimal amount, PaymentStatus status, LocalDateTime paidAt) {
        this.id = id;
        this.registration = registration;
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
    }

    public PaymentsEntity() {
    }

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
        // Calcula el monto automáticamente
        if (this.amount == null &&
                registration != null &&
                registration.getPrice() != null &&
                registration.getQuantity() != null) {
            this.amount = registration.getPrice().multiply(new BigDecimal(registration.getQuantity()));
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}
