package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.SatisfactionSurveyRequestDTO;
import com.ticketlite.demo.model.SatisfactionSurveyEntity;
import com.ticketlite.demo.service.EventAnalyticsService;
import com.ticketlite.demo.service.SatisfactionSurveyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/public/events/satisfaction")
public class SatisfactionSurveyController {
    private final SatisfactionSurveyService surveyService;
    private final EventAnalyticsService eventAnalyticsService;

    @Autowired
    public SatisfactionSurveyController(EventAnalyticsService eventAnalyticsService, SatisfactionSurveyService surveyService) {
        this.surveyService = surveyService;
        this.eventAnalyticsService = eventAnalyticsService;
    }

    @PostMapping
    public ResponseEntity<?> submitSurvey(@RequestBody @Valid SatisfactionSurveyRequestDTO dto) {
        SatisfactionSurveyEntity saved = surveyService.saveOrUpdateSurvey(dto);

        try {
            BigDecimal rating = BigDecimal.valueOf(dto.getRating());
            var updatedAnalytics = eventAnalyticsService.updateEsta(dto.getEventId(), "satisfaction", rating);

            return ResponseEntity.ok(Map.of(
                    "message", "Encuesta procesada con éxito",
                    "surveyId", saved.getId(),
                    "updatedAnalytics", updatedAnalytics
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar las estadísticas: " + e.getMessage());
        }
    }

}
