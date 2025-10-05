package dev.indian.snowball.rule;

import dev.indian.snowball.model.strategy.StrategyFragment;
import dev.indian.snowball.model.strategy.TradingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.NotRule;
import org.ta4j.core.rules.OrRule;

import java.util.List;
import java.util.Map;

@Component
public class TradingStrategyRuleParser {
    private final Map<String, RuleBuilder> ruleBuilders;

    @Autowired
    public TradingStrategyRuleParser(Map<String, RuleBuilder> ruleBuilders) {
        this.ruleBuilders = ruleBuilders;
    }

    public Rule parseBuy(TradingStrategy strategy, BarSeries series) {
        if (strategy.getBuyFragment() == null) {
            throw new IllegalArgumentException("No buyFragment defined in strategy");
        }
        return parseFragment(strategy.getBuyFragment(), series);
    }

    public Rule parseSell(TradingStrategy strategy, BarSeries series) {
        if (strategy.getSellFragment() == null) {
            throw new IllegalArgumentException("No sellFragment defined in strategy");
        }
        return parseFragment(strategy.getSellFragment(), series);
    }

    /**
     * Deprecated: Use parseBuy or parseSell instead.
     */
    @Deprecated
    public Rule parse(TradingStrategy strategy, BarSeries series) {
        throw new UnsupportedOperationException("Use parseBuy or parseSell for TradingStrategy with buy/sell fragments");
    }

    private Rule parseFragment(StrategyFragment fragment, BarSeries series) {
        if (fragment.isLeaf()) {
            String indicator = fragment.getIndicatorName();
            Map<String, Object> params = fragment.getParameters();
            RuleBuilder builder = ruleBuilders.get(indicator);
            if (builder == null) {
                throw new IllegalArgumentException("No RuleBuilder found for indicator: " + indicator);
            }
            return builder.build(params, series);
        } else {
            List<StrategyFragment> children = fragment.getFragments();
            switch (fragment.getOperation()) {
                case AND:
                    Rule andRule = parseFragment(children.get(0), series);
                    for (int i = 1; i < children.size(); i++) {
                        andRule = new AndRule(andRule, parseFragment(children.get(i), series));
                    }
                    return andRule;
                case OR:
                    Rule orRule = parseFragment(children.get(0), series);
                    for (int i = 1; i < children.size(); i++) {
                        orRule = new OrRule(orRule, parseFragment(children.get(i), series));
                    }
                    return orRule;
                case NOT:
                    if (children.size() != 1) {
                        throw new IllegalArgumentException("NOT operation must have exactly one child");
                    }
                    return new NotRule(parseFragment(children.get(0), series));
                default:
                    throw new UnsupportedOperationException("Unknown operation: " + fragment.getOperation());
            }
        }
    }
}
