package com.ticketlite.demo.service;


import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.structure.exception.ConflictException;
import com.ticketlite.demo.structure.exception.NotFoundException;
import jakarta.transaction.Transactional;
import jdk.jfr.Event;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventsService {

    //Atributos

    private EventsRepository eventsRepository;
    //Location
    private final GeometryFactory geometryFactory;


    //Importante para conectar el repository
    @Autowired
    //Constructor
    public EventsService(EventsRepository eventsRepository, GeometryFactory geometryFactory) {
        this.eventsRepository = eventsRepository;
        this.geometryFactory = geometryFactory;
    }

    //Metodos
    //Get All
    public List<EventsEntity> getAllEvents(){
        List<EventsEntity> events = eventsRepository.findAll();
        return events;
    }

    //Get Only
    public Optional<EventsEntity> getById(Long eventId){
        Optional<EventsEntity> event = eventsRepository.findById(eventId);
        return event;
    }
    //Get for location
    public List<EventsEntity> getEventsNearby(double lat, double lon, double radius) {
        return eventsRepository.findEventsNearby(lat, lon, radius);
    }
    
    //Post

    public String saveEvent(EventsEntity event ) throws ConflictException {
        try {
            if (event.getLatitude() != null && event.getLongitude() != null) {
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point location = geometryFactory.createPoint(new Coordinate(event.getLongitude(), event.getLatitude()));
                event.setLocation(location);
            } else {
                throw new RuntimeException("Latitud y longitud son obligatorios para crear la ubicación del evento.");
            }

            event.setName(event.getName());
            event.setDescription(event.getDescription());
            event.setStartDate(event.getStartDate());
            event.setEndDate(event.getEndDate());
            event.setCategory(event.getCategory());
            event.setImageUrl(event.getImageUrl());
            event.setStatus(event.getStatus());
            event.setAddress(event.getAddress());

            eventsRepository.save(event);
            return "Se creo correctamente el evento: " + event.getName();
        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el evento: " + e.getMessage(), e);
        }
    }
    //PUT
    @Transactional

    public EventsEntity updateEvent(Long eventId, EventsEntity editEvent)  throws NotFoundException {
        try {

            EventsEntity existingEvent = eventsRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado con id " + eventId));


            existingEvent.setName(editEvent.getName());
            existingEvent.setDescription(editEvent.getDescription());
            existingEvent.setStartDate(editEvent.getStartDate());
            existingEvent.setEndDate(editEvent.getEndDate());
            existingEvent.setCategory(editEvent.getCategory());
            existingEvent.setStatus(editEvent.getStatus());
            existingEvent.setImageUrl(editEvent.getImageUrl());
            existingEvent.setAddress(editEvent.getAddress());

            if (editEvent.getLatitude() != null && editEvent.getLongitude() != null) {
                Point newLocation = geometryFactory.createPoint(new Coordinate(editEvent.getLongitude(), editEvent.getLatitude()));
                newLocation.setSRID(4326);
                existingEvent.setLocation(newLocation);
            }

            return eventsRepository.save(existingEvent);

        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al actualizar el evento: " + e.getMessage(), e);
        }
    }
    //DELETE

    public void deleteEvent (Long id){
        if (eventsRepository.existsById(id)){
            eventsRepository.deleteById(id);
        }else {
            throw new RuntimeException("Evento no encontrado por ID: " + id);
        }
    }
}
