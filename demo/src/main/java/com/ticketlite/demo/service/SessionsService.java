package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.SessionsEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.SessionsRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SessionsService {

    //Atributos

    private SessionsRepository sessionsRepository;
    private EventsRepository eventsRepository;
    //Importante para conectar el repository
    @Autowired
    //Constructor

    public SessionsService(SessionsRepository sessionsRepository, EventsRepository eventsRepository) {
        this.sessionsRepository = sessionsRepository;
        this.eventsRepository = eventsRepository;
    }

    //Metodos
    //GET BY ID
    public SessionsEntity getById(Long sessionId){

        return sessionsRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Sesión no encontrada"));
    }

    //GET BY Event Id Order By Start Time Asc
    public List<SessionsEntity> getBySessions(Long eventId) {
        return sessionsRepository.findByEventIdOrderByStartTimeAsc(eventId);
    }

    //POST
    public SessionsEntity createSession(Long eventId, SessionsEntity sessionData) {
        try {
            EventsEntity event = eventsRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

            sessionData.setEvent(event);
            return sessionsRepository.save(sessionData);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear la session: " + e.getMessage(), e);
        }
    }

    //PUT
    public SessionsEntity updateSession(Long sessionId, SessionsEntity updatedData) {
        try {
            SessionsEntity udSession = sessionsRepository.findById(sessionId).orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

            udSession.setTitle(updatedData.getTitle());
            udSession.setDescription(updatedData.getDescription());
            udSession.setStartTime(updatedData.getStartTime());
            udSession.setEndTime(updatedData.getEndTime());
            udSession.setSpeakerName(updatedData.getSpeakerName());
            udSession.setSpeakerBio(updatedData.getSpeakerBio());

            return sessionsRepository.save(udSession);
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al actualizar la session: " + e.getMessage(), e);
        }
    }

    //DELETE
    public void deleteSession (Long sessionId){
        if (sessionsRepository.existsById(sessionId)){
            sessionsRepository.deleteById(sessionId);
        }else {
            throw new RuntimeException("Session no encontrada por ID: " + sessionId);
        }
    }
}
