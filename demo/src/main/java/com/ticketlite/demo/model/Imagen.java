package com.ticketlite.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "imagenes")
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_original", nullable = false)
    private String nombreOriginal;

    @Column(name = "key_s3", nullable = false, unique = true)
    private String keyS3;

    @ManyToOne
    @JoinColumn(name = "id_events", nullable = false)
    private EventsEntity events;

    // Constructores
    public Imagen() {
    }

    public Imagen(Long id, String nombreOriginal, String keyS3, EventsEntity events) {
        this.id = id;
        this.nombreOriginal = nombreOriginal;
        this.keyS3 = keyS3;
        this.events = events;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getKeyS3() {
        return keyS3;
    }

    public void setKeyS3(String keyS3) {
        this.keyS3 = keyS3;
    }

    public EventsEntity getEvents() {
        return events;
    }

    public void setEvents(EventsEntity events) {
        this.events = events;
    }
}
