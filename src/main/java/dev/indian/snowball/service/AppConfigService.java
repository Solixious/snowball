package dev.indian.snowball.service;

import dev.indian.snowball.constants.AppConfigKey;
import dev.indian.snowball.model.entity.AppConfigEntity;
import dev.indian.snowball.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppConfigService {
    private final AppConfigRepository appConfigRepository;

    public Optional<String> getConfigValue(AppConfigKey key) {
        return appConfigRepository.findByConfigKey(key).map(AppConfigEntity::getConfigValue);
    }

    public void setConfigValue(AppConfigKey key, String value) {
        AppConfigEntity entity = appConfigRepository.findByConfigKey(key)
                .orElse(new AppConfigEntity(key, value));
        entity.setConfigValue(value);
        appConfigRepository.save(entity);
    }
}


