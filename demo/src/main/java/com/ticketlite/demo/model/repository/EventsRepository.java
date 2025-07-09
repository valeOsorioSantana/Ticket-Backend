package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.EventsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsRepository extends JpaRepository<EventsEntity,Long> {
    boolean existsById(Long id);
    List<EventsEntity> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """ 
            SELECT * FROM events WHERE ST_DWithin( location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radius) 
           """, nativeQuery = true)
    List<EventsEntity> findEventsNearby(@Param("lat") double lat, @Param("lon") double lon, @Param("radius") double radiusInMeters);

    boolean existsByName(String name);
}
