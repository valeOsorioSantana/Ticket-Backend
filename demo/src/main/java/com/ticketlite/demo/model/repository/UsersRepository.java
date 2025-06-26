package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<UsersEntity,Long> {
    boolean existsById(Long id);

    boolean existsByEmail(String email);
}
