package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.PasswordTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordTokenCrudRepository extends JpaRepository<PasswordTokenEntity,Long> {

    void deleteByExpirationBefore(LocalDateTime fecha);
    Optional<PasswordTokenEntity> findByToken(String token);
}