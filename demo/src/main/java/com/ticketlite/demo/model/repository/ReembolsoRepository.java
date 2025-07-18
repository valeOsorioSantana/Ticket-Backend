package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.DTO.ReembolsoRespuestaDTO;
import com.ticketlite.demo.model.Reembolso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReembolsoRepository extends JpaRepository<Reembolso,Long> {

}
