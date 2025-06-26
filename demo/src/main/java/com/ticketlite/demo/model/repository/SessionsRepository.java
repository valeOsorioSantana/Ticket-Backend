package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.SessionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionsRepository extends JpaRepository<SessionsEntity,Long> {
    boolean existsById(Long id);

    List<SessionsEntity> findByEventIdOrderByStartTimeAsc(Long eventId);
}
