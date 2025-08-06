package com.ticketlite.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"users_id", "events_id"})
})
public class FavoritoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "users_id")
    private UsersEntity users;

    @ManyToOne(optional = false)
    @JoinColumn(name = "events_id")
    private EventsEntity events;

    @Column(name = "fecha_fav", nullable = false)
    private LocalDateTime fechaFav = LocalDateTime.now();

    //Constructor

    public FavoritoEntity(Long id, UsersEntity users, EventsEntity events, LocalDateTime fechaFav) {
        this.id = id;
        this.users = users;
        this.events = events;
        this.fechaFav = fechaFav;
    }

    public FavoritoEntity() {
    }
// Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersEntity getUsers() {
        return users;
    }

    public void setUsers(UsersEntity users) {
        this.users = users;
    }

    public EventsEntity getEvents() {
        return events;
    }

    public void setEvents(EventsEntity events) {
        this.events = events;
    }

    public LocalDateTime getFechaFav() {
        return fechaFav;
    }

    public void setFechaFav(LocalDateTime fechaFav) {
        this.fechaFav = fechaFav;
    }
}
