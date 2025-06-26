package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.TicketsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketsRepository extends JpaRepository<TicketsEntity,Long> {
    Optional<Object> findByRegistrationId(Long registrationId);
}
