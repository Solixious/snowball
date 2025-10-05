package dev.indian.snowball.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradingStrategy {
    private String name;
    private String description;
    private StrategyFragment fragment;
}
