package com.ticketlite.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Table(name = "satisfaction_surveys")
public class SatisfactionSurveyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long eventId;
    private Long userId;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;  // 1-5
    @Column(length = 1000)
    private String comments;
    private LocalDateTime submittedAt;

    // Constructores

    @Autowired
    public SatisfactionSurveyEntity(Long id, Long eventId,Long userId, Integer rating, String comments, LocalDateTime submittedAt) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.submittedAt = submittedAt;
    }

    public SatisfactionSurveyEntity() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
