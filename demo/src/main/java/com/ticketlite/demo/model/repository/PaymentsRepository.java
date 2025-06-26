package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.PaymentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentsRepository extends JpaRepository<PaymentsEntity,Long> {

    Optional<PaymentsEntity> findByRegistrationId(Long registrationId);
}
