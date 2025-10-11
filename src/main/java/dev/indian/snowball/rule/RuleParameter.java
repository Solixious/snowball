package dev.indian.snowball.rule;

import java.util.List;

public record RuleParameter(String name, Class<?> type, boolean mandatory, String description,
                            List<String> allowedValues) {
    public RuleParameter(String name, Class<?> type, boolean mandatory, String description) {
        this(name, type, mandatory, description, null);
    }
}
