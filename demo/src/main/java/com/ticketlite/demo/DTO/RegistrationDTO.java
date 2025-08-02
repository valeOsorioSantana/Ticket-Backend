package com.ticketlite.demo.DTO;

import java.time.LocalDateTime;

public class RegistrationDTO {
    private Long id;
    private Long userId;
    private Long eventId;
    private String ticketType;
    private int quantity;
    private LocalDateTime registeredAt;

    // Constructor completo
    public RegistrationDTO(Long id, Long userId, Long eventId, String ticketType, int quantity, LocalDateTime registeredAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.ticketType = ticketType;
        this.quantity = quantity;
        this.registeredAt = registeredAt;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
