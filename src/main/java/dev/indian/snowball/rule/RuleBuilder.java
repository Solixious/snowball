package dev.indian.snowball.rule;

import org.ta4j.core.Rule;
import org.ta4j.core.BarSeries;
import java.util.Map;
import java.util.List;

public interface RuleBuilder {
    Rule build(Map<String, Object> parameters, BarSeries series);
    List<RuleParameter> getParameters();
}