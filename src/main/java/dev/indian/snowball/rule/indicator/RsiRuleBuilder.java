package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.RuleBuilder;
import org.ta4j.core.Rule;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component("RSI")
public class RsiRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = Integer.parseInt(parameters.get("period").toString());
        String operator = parameters.get("operator").toString();
        double value = Double.parseDouble(parameters.get("value").toString());
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), period);
        switch (operator) {
            case "<": return new UnderIndicatorRule(rsi, value);
            case ">": return new OverIndicatorRule(rsi, value);
            default: throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }
}
