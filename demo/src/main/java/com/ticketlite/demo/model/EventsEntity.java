package com.ticketlite.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ticketlite.demo.structure.json.PointDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ticketlite.demo.structure.json.PointSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Schema(description = "Modelo que representa un evento dentro del sistema")
public class EventsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único del evento", example = "1", required = true)
    private Long id;

    @Column(nullable = false, length = 255)
    @Schema(description = "Nombre del evento", example = "Concierto de Rock", required = true)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del evento", example = "Un evento musical en vivo con bandas internacionales", required = true)
    private String description;

    @Column(name = "start_date")
    @Schema(description = "Fecha de inicio del evento", example = "2025-07-15")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    @Schema(description = "Fecha de finalización del evento", example = "2025-07-16")
    private LocalDateTime endDate;

    @JsonSerialize(using = PointSerializer.class)
    @JsonDeserialize(using = PointDeserializer.class)
    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    @Schema(description = "Ubicación geográfica del evento como coordenadas", required = true)
    private Point location;

    @Column(nullable = false, length = 100)
    @Schema(description = "Categoría del evento", example = "Música", required = true)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado actual del evento", example = "Publicado", required = true,
            allowableValues = {"Publicado", "Borrador", "Cancelado", "Finalizado"})
    private EventStatus status;

    @Column(length = 150)
    private String address;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    @Schema(description = "Fecha y hora de creación del evento en el sistema", example = "2025-06-12T15:30:00")
    private LocalDateTime createdAt;

    @Column(name = "ticket_price")
    private BigDecimal ticketPrice;

    // Campos temporales para recibir latitude/longitude en el JSON
    @Transient
    @JsonIgnore
    private Double latitude;

    @Transient
    @JsonIgnore
    private Double longitude;

    public enum EventStatus {
        PUBLICADO,
        BORRADOR,
        CANCELADO,
        FINALIZADO
    }


    //Constructor

    public EventsEntity(Long id, String name, String description, LocalDateTime startDate, LocalDateTime endDate, Point location, String category, EventStatus status, String address, LocalDateTime createdAt, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.category = category;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public EventsEntity() {
    }

    //Getters y setters

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

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
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

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

}
