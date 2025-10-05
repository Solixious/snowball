package dev.indian.snowball.model.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrategyFragment {
    private boolean leaf;
    // For leaf nodes
    private String indicatorName;
    private Map<String, Object> parameters;
    // For non-leaf nodes
    private StrategyOperation operation; // AND, OR, etc.
    private List<StrategyFragment> fragments;
}
