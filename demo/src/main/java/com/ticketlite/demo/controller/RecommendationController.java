package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recomendaciones de Eventos", description = "Proporciona eventos recomendados a los usuarios según sus preferencias y comportamiento")
public class RecommendationController {

    private UsersRepository usersRepository;
    private RecommendationService recommendationService;

    @Autowired

    public RecommendationController(UsersRepository usersRepository, RecommendationService recommendationService) {
        this.usersRepository = usersRepository;
        this.recommendationService = recommendationService;
    }

    @Operation(summary = "Obtener recomendaciones de eventos",
            description = "Recupera una lista de eventos recomendados para un usuario específico en base a su historial y preferencias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos recomendados obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<List<EventsEntity>> getRecommendations(@Parameter(description = "ID del usuario para obtener recomendaciones", required = true) @PathVariable Long userId) {
        UsersEntity user = usersRepository.findById(userId).orElseThrow();
        List<EventsEntity> recommendations = recommendationService.getRecommendedEvents(user);
        return ResponseEntity.ok(recommendations);
    }
}
