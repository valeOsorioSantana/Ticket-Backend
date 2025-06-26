package com.ticketlite.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sessions")
public class SessionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    @Schema(description = "Identificador único de la sesión", example = "1", required = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @Schema(description = "Evento asociado a la sesión", required = true)
    private EventsEntity event;

    @Column(length = 255, nullable = false)
    @Schema(description = "Título de la sesión", example = "Charla sobre Inteligencia Artificial", required = true)
    private String title;

    @Schema(description = "Descripción de la sesión", example = "Una introducción al estado actual de la IA.")
    private String description;

    @Column(name = "start_time")
    @Schema(description = "Fecha de inicio de la sesión", example = "2025-07-01")
    private LocalDate startTime;

    @Column(name = "end_time")
    @Schema(description = "Fecha de fin de la sesión", example = "2025-07-01")
    private LocalDate endTime;

    @Column(name = "speaker_name", length = 255)
    @Schema(description = "Nombre del orador", example = "Juan Pérez")
    private String speakerName;

    @Column(name = "speaker_bio")
    @Schema(description = "Biografía del orador", example = "Experto en tecnologías emergentes con 10 años de experiencia.")
    private String speakerBio;

    // Constructor
    public SessionsEntity(Long id, EventsEntity event, String title, String description, LocalDate startTime, LocalDate endTime, String speakerName, String speakerBio) {
        this.id = id;
        this.event = event;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.speakerName = speakerName;
        this.speakerBio = speakerBio;
    }

    public SessionsEntity() {}

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public EventsEntity getEvent() { return event; }

    public void setEvent(EventsEntity event) { this.event = event; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartTime() { return startTime; }

    public void setStartTime(LocalDate startTime) { this.startTime = startTime; }

    public LocalDate getEndTime() { return endTime; }

    public void setEndTime(LocalDate endTime) { this.endTime = endTime; }

    public String getSpeakerName() { return speakerName; }

    public void setSpeakerName(String speakerName) { this.speakerName = speakerName; }

    public String getSpeakerBio() { return speakerBio; }

    public void setSpeakerBio(String speakerBio) { this.speakerBio = speakerBio; }
}
