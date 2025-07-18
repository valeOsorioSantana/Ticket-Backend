package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.PoliticaCancelacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticaCancelacionRepository extends JpaRepository<PoliticaCancelacion, Long> {
    PoliticaCancelacion findByEventId(Long eventId);
}
