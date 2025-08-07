package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.EventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<EventsEntity,Long> {
    boolean existsById(Long id);
    List<EventsEntity> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT e FROM EventsEntity e " +
            "WHERE ST_DistanceSphere(POINT(:lon, :lat), POINT(e.longitude, e.latitude)) <= :radius",
            nativeQuery = true)
    List<EventsEntity> findEventsNearby(@Param("lat") double lat, @Param("lon") double lon, @Param("radius") double radiusInMeters);

    List<EventsEntity> findByStartDateAfter(LocalDateTime startDate);
    List<EventsEntity> findByEndDateBefore(LocalDateTime endDate);
    List<EventsEntity> findByCategoryIn(List<String> category);
    List<EventsEntity> findByStatus(EventsEntity.EventStatus status);
    Optional<EventsEntity> findByName(String name);

    List<EventsEntity> findByStatusAndStartDateBetween(EventsEntity.EventStatus status, LocalDateTime start, LocalDateTime end);

    boolean existsByName(String name);
}
