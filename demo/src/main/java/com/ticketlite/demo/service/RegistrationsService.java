package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.RegistrationsRepository;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationsService {

    //Atributos

    private RegistrationsRepository registrationsRepository;
    private UsersRepository usersRepository;
    private EventsRepository eventsRepository;
    //Importante para conectar el repository
    @Autowired
    public RegistrationsService(RegistrationsRepository registrationsRepository, UsersRepository usersRepository, EventsRepository eventsRepository) {
        this.registrationsRepository = registrationsRepository;
        this.usersRepository = usersRepository;
        this.eventsRepository = eventsRepository;
    }

    //Metodos
    //GET BY ID
    //obtener registros por usuario
    public List<RegistrationsEntity> getRegistrationsByUser(Long userId) {
        return registrationsRepository.findByUsersId(userId);
    }
    //obtener registros por evento
    public List<RegistrationsEntity> getRegistrationsByEvent(Long eventId) {
        return registrationsRepository.findByEventsId(eventId);
    }

    //POST
    //Crear registro
    public RegistrationsEntity register(RegistrationsEntity registration) {
        try {
            Long userId = registration.getUsers().getId();
            Long eventId = registration.getEvents().getId();

            UsersEntity user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            EventsEntity event = eventsRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

            registration.setUsers(user);
            registration.setEvents(event);
            registration.setTicketType(registration.getTicketType());
            registration.setPrice(registration.getPrice());
            registration.setStatus(registration.getStatus());
            registration.setReminderDeliveryStatus(registration.getReminderDeliveryStatus());
            registration.setRegisteredAt(LocalDateTime.now());

            registration.setId(null);
            return registrationsRepository.save(registration);

        }catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el registro: " + e.getMessage(), e);
        }
    }

    //PUT
    public RegistrationsEntity updateRegistrationDetails(RegistrationsEntity registration) {
        Long registrationId =registration.getId();
        registrationsRepository.findById(registrationId).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        RegistrationsEntity.RegistrationStatus status = registration.getStatus();
        if (status != null) {
            registration.setStatus(status);
        }
        RegistrationsEntity.ReminderStatus markReminderAsSent = registration.getReminderDeliveryStatus();
        LocalDateTime reminderTime = registration.getReminderTime();
        if (Boolean.TRUE.equals(markReminderAsSent)) {
            registration.setReminderDeliveryStatus(RegistrationsEntity.ReminderStatus.Recordado);
            registration.setReminderTime(reminderTime != null ? reminderTime : LocalDateTime.now());
        }

        Long userId = registration.getUsers().getId();
        Long eventId = registration.getEvents().getId();

        UsersEntity user = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        EventsEntity event = eventsRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        registration.setUsers(user);
        registration.setEvents(event);
        registration.setTicketType(registration.getTicketType());
        registration.setPrice(registration.getPrice());
        registration.setStatus(registration.getStatus());
        registration.setReminderDeliveryStatus(registration.getReminderDeliveryStatus());
        registration.setRegisteredAt(LocalDateTime.now());

        return registrationsRepository.save(registration);
    }

    //DELETE
    public void deleteRegistration (Long registrationId){
        if (registrationsRepository.existsById(registrationId)){
            registrationsRepository.deleteById(registrationId);
        }else {
            throw new RuntimeException("Registro no encontrado por ID: " + registrationId);
        }
    }


    public void deleteAllByEventsId (Long id) {
        registrationsRepository.deleteAllByEventsId(id);
    }

}
