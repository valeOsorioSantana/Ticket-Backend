package com.ticketlite.demo.service;

import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.model.TicketsEntity;
import com.ticketlite.demo.model.repository.RegistrationsRepository;
import com.ticketlite.demo.model.repository.TicketsRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketsService {

    //Atributos

    private TicketsRepository ticketsRepository;
    private RegistrationsRepository registrationsRepository;
    //Importante para conectar el repository
    @Autowired
    //Constructor
    public TicketsService(TicketsRepository ticketsRepository, RegistrationsRepository registrationsRepository) {
        this.ticketsRepository = ticketsRepository;
        this.registrationsRepository = registrationsRepository;
    }

    //Metodos
    //GET ALL
    public List<TicketsEntity> getAllTickets() {
        return ticketsRepository.findAll();
    }

    //GET BY ID
    //obtener ticket por ID
    public TicketsEntity getTicketById(Long ticketId) {
        return ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
    }
    //obtener ticket por ID de registro
    public TicketsEntity getTicketByRegistration(Long registrationId) {
        return (TicketsEntity) ticketsRepository.findByRegistrationId(registrationId).orElseThrow(() -> new RuntimeException("Ticket no encontrado para el registro"));
    }

    //POST
    //crear un ticket
    public TicketsEntity createTicket(Long registrationId, String qrCode) {
        try {
            RegistrationsEntity registration = registrationsRepository.findById(registrationId).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            TicketsEntity ticket = new TicketsEntity();

            ticket.setRegistration(registration);
            ticket.setQrCode(qrCode);  // se puede agregar automaticamente, preguntar primero

            return ticketsRepository.save(ticket);

        } catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el ticket: " + e.getMessage(), e);
        }
    }

    //DELETE
    public void deleteTicket (Long ticketId){
        if (ticketsRepository.existsById(ticketId)){
            ticketsRepository.deleteById(ticketId);
        }else {
            throw new RuntimeException("Ticket no encontrado por ID: " + ticketId);
        }
    }
}
