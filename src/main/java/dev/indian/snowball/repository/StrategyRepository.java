package dev.indian.snowball.repository;

import dev.indian.snowball.model.entity.StrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StrategyRepository extends JpaRepository<StrategyEntity, Long> {
    Optional<StrategyEntity> findByName(String name);
}

