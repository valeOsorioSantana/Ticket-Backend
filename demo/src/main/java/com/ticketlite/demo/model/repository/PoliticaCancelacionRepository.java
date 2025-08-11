package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.PoliticaCancelacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PoliticaCancelacionRepository extends JpaRepository<PoliticaCancelacion, Long> {

}
