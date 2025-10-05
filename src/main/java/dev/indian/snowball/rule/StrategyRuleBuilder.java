package dev.indian.snowball.rule;

import dev.indian.snowball.model.strategy.StrategyFragment;
import dev.indian.snowball.model.strategy.StrategyOperation;
import org.springframework.stereotype.Component;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;

import java.util.List;

@Component
public class StrategyRuleBuilder {
    private final RuleBuilderRegistry ruleBuilderRegistry;

    public StrategyRuleBuilder(RuleBuilderRegistry ruleBuilderRegistry) {
        this.ruleBuilderRegistry = ruleBuilderRegistry;
    }

    public Rule buildRule(StrategyFragment fragment, BarSeries series) {
        if (fragment.isLeaf()) {
            return buildLeafRule(fragment, series);
        } else {
            List<Rule> childRules = fragment.getFragments().stream()
                    .map(f -> buildRule(f, series))
                    .toList();
            StrategyOperation op = fragment.getOperation();
            switch (op) {
                case AND:
                    return childRules.stream().reduce(Rule::and).orElseThrow();
                case OR:
                    return childRules.stream().reduce(Rule::or).orElseThrow();
                case NOT:
                    if (childRules.size() != 1) throw new IllegalArgumentException("NOT must have one child");
                    return childRules.get(0).negation();
                default:
                    throw new UnsupportedOperationException("Unknown operation: " + op);
            }
        }
    }

    private Rule buildLeafRule(StrategyFragment fragment, BarSeries series) {
        String indicatorName = fragment.getIndicatorName();
        return ruleBuilderRegistry.get(indicatorName).build(fragment.getParameters(), series);
    }
}
