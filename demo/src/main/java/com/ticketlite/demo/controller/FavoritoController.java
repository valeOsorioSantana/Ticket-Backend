package com.ticketlite.demo.controller;


import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.FavoritoEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios/{usersId}/favoritos")
public class FavoritoController {

    private FavoritoService favoritoService;
    private UsersRepository usersRepository;

    @Autowired
    public FavoritoController(UsersRepository usersRepository,FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/favoritos/{userId}")
    public ResponseEntity<List<EventsEntity>> obtenerFavoritos(@PathVariable Long userId) {
        List<FavoritoEntity> favoritos = favoritoService.getFav(userId);
        List<EventsEntity> eventos = favoritos.stream()
                .map(FavoritoEntity::getEvents)
                .toList();

        return ResponseEntity.ok(eventos);
    }

    @PostMapping
    public ResponseEntity<Void> agregarFavorito(
            @PathVariable Long usersId,
            @RequestBody FavoritoRequest request) {
        favoritoService.addAFav(usersId, request.getEventId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventsId}")
    public ResponseEntity<Void> eliminarFavorito(
            @PathVariable Long usersId,
            @PathVariable Long eventsId) {
        favoritoService.deleteFav(usersId, eventsId);
        return ResponseEntity.ok().build();
    }

    // DTO para el cuerpo del POST
    public static class FavoritoRequest {
        private Long eventId;

        public Long getEventId() {
            return eventId;
        }

        public void setEventId(Long eventId) {
            this.eventId = eventId;
        }
    }
}
