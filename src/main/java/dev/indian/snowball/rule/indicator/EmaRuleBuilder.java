package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.RuleBuilder;
import org.ta4j.core.Rule;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import dev.indian.snowball.rule.IndicatorRuleUtil;
import dev.indian.snowball.rule.RuleParameter;
import dev.indian.snowball.rule.RuleParamUtil;
import dev.indian.snowball.rule.RuleParamKeys;
import java.util.Map;
import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component("EMA")
public class EmaRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = RuleParamUtil.getInt(parameters, RuleParamKeys.EMA_PERIOD_KEYS);
        String operator = RuleParamUtil.getString(parameters, RuleParamKeys.OPERATOR_KEYS);
        double value = RuleParamUtil.getDouble(parameters, RuleParamKeys.THRESHOLD_KEYS);
        EMAIndicator ema = new EMAIndicator(new ClosePriceIndicator(series), period);
        return IndicatorRuleUtil.buildRule(ema, operator, value);
    }

    @Override
    public List<RuleParameter> getParameters() {
        return Arrays.asList(
            new RuleParameter(RuleParamKeys.PERIOD, Integer.class, true, "EMA period (alias: '" + RuleParamKeys.EMA_PERIOD + "')"),
            new RuleParameter(RuleParamKeys.COMPARISON, String.class, true, "Comparison operator: '<' or '>' (aliases: '" + RuleParamKeys.OPERATOR + "', '" + RuleParamKeys.OP + "')", IndicatorRuleUtil.OPERATOR_OPTIONS),
            new RuleParameter(RuleParamKeys.THRESHOLD, Double.class, true, "Threshold value for comparison (alias: '" + RuleParamKeys.VALUE + "')")
        );
    }
}
