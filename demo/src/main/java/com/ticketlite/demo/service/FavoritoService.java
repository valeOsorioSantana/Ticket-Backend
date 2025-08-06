package com.ticketlite.demo.service;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.FavoritoEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.FavoritoRepository;
import com.ticketlite.demo.model.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class FavoritoService {
    private FavoritoRepository favoritoRepository;
    private UsersRepository usersRepository;
    private EventsRepository eventRepository;

    @Autowired
    public FavoritoService(FavoritoRepository favoritoRepository, UsersRepository usersRepository, EventsRepository eventRepository) {
        this.favoritoRepository = favoritoRepository;
        this.usersRepository = usersRepository;
        this.eventRepository = eventRepository;
    }

    public List<FavoritoEntity> getFav(Long usersId) {
        return favoritoRepository.findByUsersId(usersId);
    }

    public void addAFav(Long userId, Long eventId) {
        if (favoritoRepository.existsByUsersIdAndEventsId(userId, eventId)) {
            throw new RuntimeException("El evento ya está en favoritos");
        }

        UsersEntity usuario = usersRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        EventsEntity evento = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        FavoritoEntity favorito = new FavoritoEntity();
        favorito.setUsers(usuario);
        favorito.setEvents(evento);
        favoritoRepository.save(favorito);
    }

    public void deleteFav(Long usersId, Long eventsId) {
        favoritoRepository.deleteByUsersIdAndEventsId(usersId, eventsId);
    }
}
