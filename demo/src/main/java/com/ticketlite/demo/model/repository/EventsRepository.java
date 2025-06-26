package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.EventsEntity;
import jdk.jfr.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventsRepository extends JpaRepository<EventsEntity,Long> {
    boolean existsById(Long id);

    @Query(value = """ 
            SELECT * FROM events WHERE ST_DWithin( location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography, :radius) 
           """, nativeQuery = true)
    List<EventsEntity> findEventsNearby(@Param("lat") double lat, @Param("lon") double lon, @Param("radius") double radiusInMeters);
}
