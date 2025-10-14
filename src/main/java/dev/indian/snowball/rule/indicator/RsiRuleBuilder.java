package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.IndicatorRuleUtil;
import dev.indian.snowball.rule.RuleBuilder;
import dev.indian.snowball.rule.RuleParameter;
import dev.indian.snowball.rule.RuleParamUtil;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Component("RSI")
public class RsiRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = RuleParamUtil.getInt(parameters, "period", "rsiPeriod");
        String operator = RuleParamUtil.getString(parameters, "operator", "comparison", "op");
        double value = RuleParamUtil.getDouble(parameters, "value", "threshold");
        RSIIndicator rsi = new RSIIndicator(new ClosePriceIndicator(series), period);
        return IndicatorRuleUtil.buildRule(rsi, operator, value);
    }

    @Override
    public List<RuleParameter> getParameters() {
        return Arrays.asList(
            new RuleParameter("period", Integer.class, true, "RSI period (aliases: 'rsiPeriod')"),
            new RuleParameter("comparison", String.class, true, "Comparison operator: '<' or '>' (aliases: 'operator', 'op')", IndicatorRuleUtil.OPERATOR_OPTIONS),
            new RuleParameter("threshold", Double.class, true, "Threshold value for comparison (alias: 'value')")
        );
    }
}
