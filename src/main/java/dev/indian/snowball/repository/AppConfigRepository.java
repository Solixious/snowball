package dev.indian.snowball.repository;

import dev.indian.snowball.constants.AppConfigKey;
import dev.indian.snowball.model.entity.AppConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppConfigRepository extends JpaRepository<AppConfigEntity, Long> {
    Optional<AppConfigEntity> findByConfigKey(AppConfigKey configKey);
}

