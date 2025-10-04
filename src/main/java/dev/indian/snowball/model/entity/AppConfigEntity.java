package dev.indian.snowball.model.entity;

import dev.indian.snowball.constants.AppConfigKey;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "app_config")
public class AppConfigEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private AppConfigKey configKey;

    @Column(name = "config_value", nullable = false, length = 500)
    private String configValue;

    public AppConfigEntity(AppConfigKey configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }
}


