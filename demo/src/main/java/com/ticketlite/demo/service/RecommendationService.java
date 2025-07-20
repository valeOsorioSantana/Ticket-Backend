package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.RegistrationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private RegistrationsRepository registrationsRepo;
    private EventsRepository eventsRepo;

    @Autowired

    public RecommendationService(RegistrationsRepository registrationsRepo, EventsRepository eventsRepo) {
        this.registrationsRepo = registrationsRepo;
        this.eventsRepo = eventsRepo;
    }

    public RecommendationService() {
    }

    public List<EventsEntity> getRecommendedEvents(UsersEntity user) {
        // Obtener los registros del usuario
        List<RegistrationsEntity> registrations = registrationsRepo.findByUsersId(user.getId());

        // Contar categorías de los eventos registrados
        Map<String, Long> categoryCounts = registrations.stream()
                .map(reg -> reg.getEvents().getCategory())
                .collect(Collectors.groupingBy(cat -> cat, Collectors.counting()));

        // Obtener las categorías más frecuentes
        List<String> topCategories = categoryCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        // Recomendar eventos que estén en esas categorías
        return eventsRepo.findByCategoryIn(topCategories);
    }
}
