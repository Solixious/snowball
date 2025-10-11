package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.RuleBuilder;
import org.ta4j.core.Rule;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import dev.indian.snowball.rule.IndicatorRuleUtil;
import dev.indian.snowball.rule.RuleParameter;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component("SMA")
public class SmaRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = Integer.parseInt(parameters.get("period").toString());
        String operator = parameters.get("operator").toString();
        double value = Double.parseDouble(parameters.get("value").toString());
        SMAIndicator sma = new SMAIndicator(new ClosePriceIndicator(series), period);
        return IndicatorRuleUtil.buildRule(sma, operator, value);
    }

    @Override
    public List<RuleParameter> getParameters() {
        return Arrays.asList(
            new RuleParameter("period", Integer.class, true, "SMA period (number of bars)"),
            new RuleParameter("operator", String.class, true, "Comparison operator: '<' or '>'", IndicatorRuleUtil.OPERATOR_OPTIONS),
            new RuleParameter("value", Double.class, true, "Threshold value for comparison")
        );
    }
}
