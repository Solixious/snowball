package dev.indian.snowball.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradingStrategy {
    private String name;
    private String description;
    private StrategyFragment buyFragment;
    private StrategyFragment sellFragment;
}
