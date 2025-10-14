package dev.indian.snowball.rule;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

import java.util.List;
import java.util.Map;

public interface RuleBuilder {
    Rule build(Map<String, Object> parameters, BarSeries series);
    List<RuleParameter> getParameters();
}