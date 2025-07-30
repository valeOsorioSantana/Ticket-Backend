package com.ticketlite.demo.DTO;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.Imagen;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO que representa un evento")
public class EventCompleteDTO {

    @Schema(description = "ID del evento", example = "1")
    private Long id;

    @Schema(description = "Nombre del evento", example = "Concierto de Rock")
    private String name;

    @Schema(description = "Descripción del evento", example = "Un evento musical en vivo con bandas internacionales")
    private String description;

    @Schema(description = "Fecha de inicio del evento", example = "2025-07-15T20:00:00")
    private LocalDateTime startDate;

    @Schema(description = "Fecha de finalización del evento", example = "2025-07-16T02:00:00")
    private LocalDateTime endDate;

    @Schema(description = "Latitud de la ubicación del evento", example = "19.4326")
    private Double latitude;

    @Schema(description = "Longitud de la ubicación del evento", example = "-99.1332")
    private Double longitude;

    @Schema(description = "Categoría del evento", example = "Música")
    private String category;

    @Schema(description = "Estado del evento", example = "Publicado")
    private EventsEntity.EventStatus status;

    @Schema(description = "Dirección del evento", example = "Av. Reforma 123, CDMX")
    private String address;

    @Schema(description = "Fecha de creación del evento", example = "2025-06-12T15:30:00")
    private LocalDateTime createdAt;

    private List<ImagenDTO> imagenes;

    private BigDecimal ticketPrice;


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

    public EventsEntity.EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventsEntity.EventStatus status) {
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

    public List<ImagenDTO> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<ImagenDTO> imagenes) {
        this.imagenes = imagenes;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

}
