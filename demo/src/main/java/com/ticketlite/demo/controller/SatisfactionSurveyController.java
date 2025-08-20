package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.SatisfactionSurveyRequestDTO;
import com.ticketlite.demo.model.SatisfactionSurveyEntity;
import com.ticketlite.demo.service.EventAnalyticsService;
import com.ticketlite.demo.service.SatisfactionSurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Encuestas de Satisfacción", description = "Permite a los usuarios enviar encuestas de satisfacción para eventos")
public class SatisfactionSurveyController {
    private final SatisfactionSurveyService surveyService;
    private final EventAnalyticsService eventAnalyticsService;

    @Autowired
    public SatisfactionSurveyController(EventAnalyticsService eventAnalyticsService, SatisfactionSurveyService surveyService) {
        this.surveyService = surveyService;
        this.eventAnalyticsService = eventAnalyticsService;
    }

    @Operation(summary = "Enviar encuesta de satisfacción",
            description = "Guarda o actualiza una encuesta de satisfacción para un evento y actualiza las estadísticas asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encuesta procesada y estadísticas actualizadas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Datos de la encuesta inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al procesar la encuesta o actualizar estadísticas")
    })
    @PostMapping
    public ResponseEntity<?> submitSurvey(@Parameter(description = "Datos de la encuesta de satisfacción", required = true) @RequestBody @Valid SatisfactionSurveyRequestDTO dto) {
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
