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

    //Constructor

    public PoliticaCancelacion(Long id, int diasPreviosPermitidos, double porcentajeReembolso) {
        this.id = id;
        this.diasPreviosPermitidos = diasPreviosPermitidos;
        this.porcentajeReembolso = porcentajeReembolso;
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
}
