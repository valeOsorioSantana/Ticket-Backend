package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<UsersEntity,Long> {
    boolean existsById(Long id);
    boolean existsByEmail(String email);

    Optional<UsersEntity> findByEmail(String email);

    Optional<UsersEntity> getByEmail(String email);
}
