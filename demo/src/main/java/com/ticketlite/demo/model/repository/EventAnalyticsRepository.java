package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.EventAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventAnalyticsRepository extends JpaRepository<EventAnalyticsEntity, Long> {

    Optional<EventAnalyticsEntity> findByEventId(Long eventId);

    boolean existsByEventId(Long eventId);
}
