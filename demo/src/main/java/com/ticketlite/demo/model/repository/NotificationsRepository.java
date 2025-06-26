package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.NotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationsRepository extends JpaRepository<NotificationsEntity,Long> {
    List<NotificationsEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
