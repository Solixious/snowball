package dev.indian.snowball.rule;

import lombok.experimental.UtilityClass;
import org.ta4j.core.Rule;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

import java.util.List;
import java.util.Arrays;

@UtilityClass
public class IndicatorRuleUtil {
    public static final List<String> OPERATOR_OPTIONS = Arrays.asList("<", ">");

    public Rule buildRule(Indicator<Num> indicator, String operator, double value) {
        return switch (operator) {
            case "<" -> new UnderIndicatorRule(indicator, value);
            case ">" -> new OverIndicatorRule(indicator, value);
            default -> throw new UnsupportedOperationException("Unknown operator: " + operator);
        };
    }
}
