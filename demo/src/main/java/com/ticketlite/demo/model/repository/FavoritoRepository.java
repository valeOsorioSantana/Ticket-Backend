package com.ticketlite.demo.model.repository;

import com.ticketlite.demo.model.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepository extends JpaRepository<FavoritoEntity,Long> {
    List<FavoritoEntity> findByUsersId(Long usersId);

    boolean existsByUsersIdAndEventsId(Long usersId, Long eventsId);

    Optional<FavoritoEntity> findByUsersIdAndEventsId(Long usersId, Long eventsId);

    void deleteByUsersIdAndEventsId(Long usersId, Long eventsId);
}
