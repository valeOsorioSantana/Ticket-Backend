package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventAnalyticsEntity;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.repository.EventAnalyticsRepository;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class EventAnalyticsService {
    //Atributos

    private EventAnalyticsRepository eventAnalyticsRepository;
    private EventsRepository eventsRepository;

    //Importante para conectar el repository
    @Autowired
    public EventAnalyticsService(EventAnalyticsRepository eventAnalyticsRepository, EventsRepository eventsRepository) {
        this.eventAnalyticsRepository = eventAnalyticsRepository;
        this.eventsRepository = eventsRepository;
    }

    //Metodos

    //GET BY ID EVENT

    public Optional<EventAnalyticsEntity> getByIdEvent(Long eventId){

        Optional<EventAnalyticsEntity> analisis = eventAnalyticsRepository.findByEventId(eventId);

        return analisis;
    }

    //POST
    //crear estadisticas
    public EventAnalyticsEntity createEst(Long eventId, EventAnalyticsEntity analytics) {
        try {
            boolean exists = eventAnalyticsRepository.existsByEventId(eventId);
            if (exists) {
                throw new IllegalStateException("Ya existe una estadística para este evento");
            }

            EventsEntity event = eventsRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));

            analytics.setEvent(event);
            return eventAnalyticsRepository.save(analytics);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear estadísticas para el evento: " + e.getMessage(), e);
        }
    }

    //PUT
    //actualizar estadisticas
    public EventAnalyticsEntity updateEsta(Long analisisId, String action, BigDecimal value) {
        try {
            EventAnalyticsEntity analisis = eventAnalyticsRepository.findById(analisisId).orElseThrow();

            switch (action.toLowerCase()) {
                case "view":
                    analisis.setTotalViews(analisis.getTotalViews() + 1);
                    break;
                case "registration":
                    analisis.setTotalRegistrations(analisis.getTotalRegistrations() + 1);
                    break;
                case "tickets":
                    analisis.setTotalTicketsSold(analisis.getTotalTicketsSold() + value.intValue());
                    break;
                case "satisfaction":
                    int previous = analisis.getTotalRegistrations();
                    BigDecimal currentAvg = analisis.getSatisfactionAvg();
                    BigDecimal totalScore = currentAvg.multiply(BigDecimal.valueOf(previous)).add(value);
                    int newTotal = previous + 1;
                    BigDecimal newAvg = totalScore.divide(BigDecimal.valueOf(newTotal), 2, RoundingMode.HALF_UP);
                    analisis.setSatisfactionAvg(newAvg);
                    analisis.setTotalRegistrations(newTotal);
                    break;
                default:
                    throw new IllegalArgumentException("Acción no válida: " + action);
            }

            return eventAnalyticsRepository.save(analisis);

        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estadísticas: " + e.getMessage());
        }
    }






}
