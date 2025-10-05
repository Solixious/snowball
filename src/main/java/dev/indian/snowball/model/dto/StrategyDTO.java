package dev.indian.snowball.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyDTO {
    private Long id;
    private String name;
    private String description;
    private String rulesJson;
    private Instant createdAt;
    private Instant updatedAt;
}
