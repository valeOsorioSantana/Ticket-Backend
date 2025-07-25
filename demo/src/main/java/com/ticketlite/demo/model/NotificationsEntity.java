package com.ticketlite.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Schema(description = "Modelo que representa una notificación enviada a un usuario")
public class NotificationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único de la notificación", example = "1", required = true)
    private Long id;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Usuario al que va dirigida la notificación", required = true)
    private UsersEntity user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    @Schema(description = "Evento relacionado con la notificación, si aplica", required = false)
    private EventsEntity event;

    @Column(name = "message_content", nullable = false)
    @Schema(description = "Contenido del mensaje de la notificación", example = "Tu evento ha sido actualizado", required = true)
    private String messageContent;

    @Column(length = 50)
    @Schema(description = "Tipo de notificación (informativa, alerta, recordatorio, etc.)", example = "alerta")
    private String type;

    @Column(name = "is_read")
    @Schema(description = "Indica si la notificación ya fue leída", example = "false")
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Fecha y hora en que se creó la notificación", example = "2025-06-17T10:15:30")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Schema(description = "Receptor de la notificación: USER o ADMIN", example = "USER")
    private String receiverType;


    // Constructores

    public NotificationsEntity(Long id, UsersEntity user, EventsEntity event, String messageContent, String type, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.messageContent = messageContent;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public NotificationsEntity() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersEntity getUser() {
        return user;
    }

    public void setUser(UsersEntity user) {
        this.user = user;
    }

    public EventsEntity getEvent() {
        return event;
    }

    public void setEvent(EventsEntity event) {
        this.event = event;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }
}