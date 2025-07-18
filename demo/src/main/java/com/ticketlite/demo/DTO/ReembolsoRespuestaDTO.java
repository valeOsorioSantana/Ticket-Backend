package com.ticketlite.demo.DTO;

import org.springframework.beans.factory.annotation.Autowired;

public class ReembolsoRespuestaDTO {
    private Long reembolsoId;
    private double montoReembolsado;
    private String estado;
    private String mensaje;

    // Constructor
    @Autowired

    public ReembolsoRespuestaDTO(Long reembolsoId, double montoReembolsado, String estado, String mensaje) {
        this.reembolsoId = reembolsoId;
        this.montoReembolsado = montoReembolsado;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    // Getters y Setters

    public Long getReembolsoId() {
        return reembolsoId;
    }

    public void setReembolsoId(Long reembolsoId) {
        this.reembolsoId = reembolsoId;
    }

    public double getMontoReembolsado() {
        return montoReembolsado;
    }

    public void setMontoReembolsado(double montoReembolsado) {
        this.montoReembolsado = montoReembolsado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
