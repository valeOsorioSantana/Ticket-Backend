package com.ticketlite.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "event_analytics")
@Schema(description = "Modelo que representa las estadísticas y análisis de un evento")
public class EventAnalyticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único del análisis", example = "1", required = true)
    private Long id;

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    @Schema(description = "Evento asociado al análisis", required = true)
    private EventsEntity event;

    @Column(name = "total_views")
    @Schema(description = "Cantidad total de visualizaciones del evento", example = "1500")
    private int totalViews = 0;

    @Column(name = "total_registrations")
    @Schema(description = "Cantidad total de registros para el evento", example = "300")
    private int totalRegistrations = 0;

    @Column(name = "total_tickets_sold")
    @Schema(description = "Total de entradas vendidas para el evento", example = "250")
    private int totalTicketsSold = 0;

    @Column(name = "satisfaction_avg", precision = 3, scale = 2)
    @Schema(description = "Promedio de satisfacción del evento basado en encuestas o calificaciones", example = "4.75")
    private BigDecimal satisfactionAvg = BigDecimal.ZERO;

    // Constructores

    public EventAnalyticsEntity(Long id, EventsEntity event, int totalViews, int totalRegistrations, int totalTicketsSold, BigDecimal satisfactionAvg) {
        this.id = id;
        this.event = event;
        this.totalViews = totalViews;
        this.totalRegistrations = totalRegistrations;
        this.totalTicketsSold = totalTicketsSold;
        this.satisfactionAvg = satisfactionAvg;
    }

    public EventAnalyticsEntity() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventsEntity getEvent() {
        return event;
    }

    public void setEvent(EventsEntity event) {
        this.event = event;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getTotalRegistrations() {
        return totalRegistrations;
    }

    public void setTotalRegistrations(int totalRegistrations) {
        this.totalRegistrations = totalRegistrations;
    }

    public int getTotalTicketsSold() {
        return totalTicketsSold;
    }

    public void setTotalTicketsSold(int totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
    }

    public BigDecimal getSatisfactionAvg() {
        return satisfactionAvg;
    }

    public void setSatisfactionAvg(BigDecimal satisfactionAvg) {
        this.satisfactionAvg = satisfactionAvg;
    }
}
