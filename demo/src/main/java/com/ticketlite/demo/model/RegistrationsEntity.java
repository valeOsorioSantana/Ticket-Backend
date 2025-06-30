package com.ticketlite.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Schema(description = "Modelo que representa una inscripción o registro de un usuario a un evento")
public class RegistrationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único del registro", example = "1", required = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "users_id", nullable = false)
    @Schema(description = "Usuario que realiza el registro", required = true)
    private UsersEntity users;

    @ManyToOne
    @JoinColumn(name = "events_id", nullable = false)
    @Schema(description = "Evento al que se registra el usuario", required = true)
    private EventsEntity events;

    @Column(name = "ticket_type", length = 100)
    @Schema(description = "Tipo de boleto seleccionado por el usuario", example = "VIP", required = false)
    private String ticketType;

    @Column(length = 38, scale = 2)
    @Schema(description = "Precio del boleto en la inscripción", example = "49.99")
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Schema(
            description = "Estado del registro",
            example = "Confirmado",
            required = true,
            allowableValues = {"Confirmado", "Cancelado", "Pendiente"}
    )
    private RegistrationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_delivery_status", nullable = false)
    @Schema(
            description = "Estado de envío del recordatorio",
            example = "NoRecordado",
            required = true,
            allowableValues = {"Recordado", "NoRecordado"}
    )
    private ReminderStatus reminderDeliveryStatus;

    @CreationTimestamp
    @Column(name = "registered_at")
    @Schema(description = "Fecha y hora en la que se realizó el registro", example = "2025-06-18T10:30:00")
    private LocalDateTime registeredAt;

    @Column(name = "reminder_time")
    @Schema(description = "Fecha y hora en la que se envió el recordatorio (si aplica)", example = "2025-07-01T08:00:00")
    private LocalDateTime reminderTime;

    // Enums

    public enum RegistrationStatus {
        Confirmado,
        Cancelado,
        Pendiente
    }

    public enum ReminderStatus {
        Recordado,
        NoRecordado
    }

    // Constructor completo

    public RegistrationsEntity(Long id, UsersEntity users, EventsEntity events, String ticketType, BigDecimal price,
                               RegistrationStatus status, ReminderStatus reminderDeliveryStatus,
                               LocalDateTime registeredAt, LocalDateTime reminderTime) {
        this.id = id;
        this.users = users;
        this.events = events;
        this.ticketType = ticketType;
        this.price = price;
        this.status = status;
        this.reminderDeliveryStatus = reminderDeliveryStatus;
        this.registeredAt = registeredAt;
        this.reminderTime = reminderTime;
    }

    public RegistrationsEntity() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersEntity getUsers() {
        return users;
    }

    public void setUsers(UsersEntity users) {
        this.users = users;
    }

    public EventsEntity getEvents() {
        return events;
    }

    public void setEvents(EventsEntity events) {
        this.events = events;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public ReminderStatus getReminderDeliveryStatus() {
        return reminderDeliveryStatus;
    }

    public void setReminderDeliveryStatus(ReminderStatus reminderDeliveryStatus) {
        this.reminderDeliveryStatus = reminderDeliveryStatus;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }
}