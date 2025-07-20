package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private UsersRepository usersRepository;
    private RecommendationService recommendationService;

    @Autowired

    public RecommendationController(UsersRepository usersRepository, RecommendationService recommendationService) {
        this.usersRepository = usersRepository;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<EventsEntity>> getRecommendations(@PathVariable Long userId) {
        UsersEntity user = usersRepository.findById(userId).orElseThrow();
        List<EventsEntity> recommendations = recommendationService.getRecommendedEvents(user);
        return ResponseEntity.ok(recommendations);
    }
}
