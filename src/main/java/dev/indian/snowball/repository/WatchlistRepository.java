package dev.indian.snowball.repository;

import dev.indian.snowball.model.entity.WatchlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<WatchlistEntity, Long> {
    Optional<WatchlistEntity> findByInstrumentToken(String instrumentToken);
}

