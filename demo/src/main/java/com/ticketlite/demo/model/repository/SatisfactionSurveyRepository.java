package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.SatisfactionSurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SatisfactionSurveyRepository extends JpaRepository<SatisfactionSurveyEntity,Long> {
    Optional<SatisfactionSurveyEntity> findByEventIdAndUserId(Long eventId, Long userId);
}
