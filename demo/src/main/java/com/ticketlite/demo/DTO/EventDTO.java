package com.ticketlite.demo.DTO;

import com.ticketlite.demo.model.EventsEntity.EventStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO que representa un evento")
public class EventDTO {

    @Schema(description = "ID del evento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Nombre del evento", example = "Concierto de Rock", required = true)
    private String name;

    @Schema(description = "Descripción del evento", example = "Un evento musical en vivo con bandas internacionales", required = true)
    private String description;

    @Schema(description = "Fecha de inicio del evento en formato ISO 8601", example = "2025-07-15T20:00:00", required = true, type = "string", format = "date-time")
    private LocalDateTime startDate;

    @Schema(description = "Fecha de finalización del evento en formato ISO 8601", example = "2025-07-16T02:00:00", required = true, type = "string", format = "date-time")
    private LocalDateTime endDate;

    @Schema(description = "Latitud de la ubicación del evento", example = "19.4326", required = true)
    private Double latitude;

    @Schema(description = "Longitud de la ubicación del evento", example = "-99.1332", required = true)
    private Double longitude;

    @Schema(description = "Categoría del evento", example = "Música", required = true)
    private String category;

    @Schema(description = "Estado del evento", example = "PUBLICADO", allowableValues = {"PUBLICADO", "BORRADOR", "CANCELADO", "FINALIZADO"}, required = true)
    private EventStatus status;

    @Schema(description = "Dirección del evento", example = "Av. Reforma 123, CDMX", required = true)
    private String address;

    @Schema(description = "Fecha de creación del evento en formato ISO 8601", example = "2025-06-12T15:30:00", accessMode = Schema.AccessMode.READ_ONLY, type = "string", format = "date-time")
    private LocalDateTime createdAt;

    public EventDTO() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
