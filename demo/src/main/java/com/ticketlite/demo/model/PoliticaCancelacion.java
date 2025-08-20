package com.ticketlite.demo.model;

import jakarta.persistence.*;

@Entity(name = "politica_cancelacion")
public class PoliticaCancelacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dias_previos_permitidos", nullable = false)
    private int diasPreviosPermitidos;
    @Column(name = "porcentaje_reembolso", nullable = false)
    private double porcentajeReembolso;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private EventsEntity event;

    //Constructor

    public PoliticaCancelacion(EventsEntity event,Long id, int diasPreviosPermitidos, double porcentajeReembolso) {
        this.id = id;
        this.diasPreviosPermitidos = diasPreviosPermitidos;
        this.porcentajeReembolso = porcentajeReembolso;
        this.event = event;
    }

    public PoliticaCancelacion() {
    }

    //Gettters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getDiasPreviosPermitidos() {
        return diasPreviosPermitidos;
    }

    public void setDiasPreviosPermitidos(int diasPreviosPermitidos) {
        this.diasPreviosPermitidos = diasPreviosPermitidos;
    }

    public double getPorcentajeReembolso() {
        return porcentajeReembolso;
    }

    public void setPorcentajeReembolso(double porcentajeReembolso) {
        this.porcentajeReembolso = porcentajeReembolso;
    }

    public EventsEntity getEvent() {
        return event;
    }

    public void setEvent(EventsEntity event) {
        this.event = event;
    }
}
