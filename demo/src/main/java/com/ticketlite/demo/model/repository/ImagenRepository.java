package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImagenRepository extends JpaRepository<Imagen, Long> {

    Optional<Imagen> findImagenByEventsId(Long idEvent);
}
