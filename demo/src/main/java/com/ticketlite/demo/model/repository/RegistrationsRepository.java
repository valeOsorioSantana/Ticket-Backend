package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.Imagen;
import com.ticketlite.demo.model.RegistrationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationsRepository extends JpaRepository<RegistrationsEntity,Long> {
    boolean existsById(Long id);

    List<RegistrationsEntity> findByUsersId(Long userId);

    List<RegistrationsEntity> findByEventsId(Long eventId);

    void deleteAllByEventsId (Long id);
}
