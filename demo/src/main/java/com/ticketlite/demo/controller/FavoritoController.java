package com.ticketlite.demo.controller;


import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.FavoritoEntity;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import com.ticketlite.demo.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Gestión de Favoritos", description = "Operaciones para administrar los eventos favoritos de un usuario")
public class FavoritoController {

    private FavoritoService favoritoService;
    private UsersRepository usersRepository;

    @Autowired
    public FavoritoController(UsersRepository usersRepository,FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
        this.usersRepository = usersRepository;
    }

    @Operation(summary = "Obtener eventos favoritos",
            description = "Devuelve todos los eventos marcados como favoritos por un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos favoritos obtenida correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventsEntity.class)))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("")
    public ResponseEntity<List<EventsEntity>> obtenerFavoritos(@PathVariable Long usersId) {
        List<FavoritoEntity> favoritos = favoritoService.getFav(usersId);
        List<EventsEntity> eventos = favoritos.stream()
                .map(FavoritoEntity::getEvents)
                .toList();
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Agregar evento a favoritos",
            description = "Agrega un evento a la lista de favoritos del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento agregado correctamente a favoritos"),
            @ApiResponse(responseCode = "404", description = "Usuario o evento no encontrado")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Identificador del evento a agregar como favorito",
            required = true,
            content = @Content(schema = @Schema(implementation = FavoritoRequest.class)))
    @PostMapping
    public ResponseEntity<Void> agregarFavorito(
            @PathVariable Long usersId,
            @RequestBody FavoritoRequest request) {
        favoritoService.addAFav(usersId, request.getEventId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar evento de favoritos",
            description = "Elimina un evento de la lista de favoritos del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento eliminado correctamente de favoritos"),
            @ApiResponse(responseCode = "404", description = "Usuario o evento no encontrado")
    })
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
