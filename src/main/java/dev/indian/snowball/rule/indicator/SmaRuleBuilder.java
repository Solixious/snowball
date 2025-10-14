package dev.indian.snowball.rule.indicator;

import dev.indian.snowball.rule.*;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component("SMA")
public class SmaRuleBuilder implements RuleBuilder {
    @Override
    public Rule build(Map<String, Object> parameters, BarSeries series) {
        int period = RuleParamUtil.getInt(parameters, RuleParamKeys.SMA_LEFT_PERIOD_KEYS);
        String operator = RuleParamUtil.getString(parameters, RuleParamKeys.OPERATOR_KEYS);
        ClosePriceIndicator close = new ClosePriceIndicator(series);
        SMAIndicator left = new SMAIndicator(close, period);

        boolean hasOther = RuleParamUtil.hasAny(parameters, RuleParamKeys.SMA_RIGHT_PERIOD_KEYS);
        boolean hasValue = RuleParamUtil.hasAny(parameters, RuleParamKeys.THRESHOLD_KEYS);

        if (hasOther == hasValue) {
            throw new IllegalArgumentException("Provide exactly one of '" + RuleParamKeys.THRESHOLD + "' (aka '" + RuleParamKeys.VALUE + "') or '" + RuleParamKeys.COMPARE_TO_PERIOD + "' (aka '" + RuleParamKeys.OTHER_PERIOD + "')");
        }

        if (hasOther) {
            int otherPeriod = RuleParamUtil.getInt(parameters, RuleParamKeys.SMA_RIGHT_PERIOD_KEYS);
            SMAIndicator right = new SMAIndicator(close, otherPeriod);
            return IndicatorRuleUtil.buildRule(left, operator, right);
        } else {
            double value = RuleParamUtil.getDouble(parameters, RuleParamKeys.THRESHOLD_KEYS);
            return IndicatorRuleUtil.buildRule(left, operator, value);
        }
    }

    @Override
    public List<RuleParameter> getParameters() {
        return Arrays.asList(
            new RuleParameter(RuleParamKeys.PERIOD, Integer.class, true, "SMA period for the primary/left leg (aliases: '" + RuleParamKeys.SMA_PERIOD + "', '" + RuleParamKeys.LEFT_PERIOD + "')"),
            new RuleParameter(RuleParamKeys.COMPARISON, String.class, true, "Comparison operator '<' or '>' (aliases: '" + RuleParamKeys.OPERATOR + "', '" + RuleParamKeys.OP + "')", IndicatorRuleUtil.OPERATOR_OPTIONS),
            new RuleParameter(RuleParamKeys.THRESHOLD, Double.class, false, "Numeric threshold to compare against (alias: '" + RuleParamKeys.VALUE + "'). Mutually exclusive with '" + RuleParamKeys.COMPARE_TO_PERIOD + "'"),
            new RuleParameter(RuleParamKeys.COMPARE_TO_PERIOD, Integer.class, false, "Compare with another SMA period (aliases: '" + RuleParamKeys.OTHER_PERIOD + "', '" + RuleParamKeys.RIGHT_PERIOD + "', '" + RuleParamKeys.COMPARE_TO_SMA_PERIOD + "'). Mutually exclusive with '" + RuleParamKeys.THRESHOLD + "')")
        );
    }
}
