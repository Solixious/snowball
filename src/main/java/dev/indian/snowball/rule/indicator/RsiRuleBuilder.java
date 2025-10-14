package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.*;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component("RSI")
public class RsiRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = RuleParamUtil.getInt(parameters, RuleParamKeys.RSI_PERIOD_KEYS);
        String operator = RuleParamUtil.getString(parameters, RuleParamKeys.OPERATOR_KEYS);
        double value = RuleParamUtil.getDouble(parameters, RuleParamKeys.THRESHOLD_KEYS);
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), period);
        return IndicatorRuleUtil.buildRule(rsi, operator, value);
    }

    @Override
    public List<RuleParameter> getParameters() {
        return Arrays.asList(
            new RuleParameter(RuleParamKeys.PERIOD, Integer.class, true, "RSI period (aliases: '" + RuleParamKeys.RSI_PERIOD + "')"),
            new RuleParameter(RuleParamKeys.COMPARISON, String.class, true, "Comparison operator: '<' or '>' (aliases: '" + RuleParamKeys.OPERATOR + "', '" + RuleParamKeys.OP + "')", IndicatorRuleUtil.OPERATOR_OPTIONS),
            new RuleParameter(RuleParamKeys.THRESHOLD, Double.class, true, "Threshold value for comparison (alias: '" + RuleParamKeys.VALUE + "')")
        );
    }
}
