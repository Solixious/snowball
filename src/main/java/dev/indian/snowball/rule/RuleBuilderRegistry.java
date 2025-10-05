package dev.indian.snowball.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleBuilderRegistry {
    private final Map<String, RuleBuilder> registry;

    @Autowired
    public RuleBuilderRegistry(Map<String, RuleBuilder> registry) {
        this.registry = registry;
    }

    public RuleBuilder get(String indicatorName) {
        RuleBuilder builder = registry.get(indicatorName.toUpperCase());
        if (builder == null) throw new UnsupportedOperationException("Unknown indicator: " + indicatorName);
        return builder;
    }
}
