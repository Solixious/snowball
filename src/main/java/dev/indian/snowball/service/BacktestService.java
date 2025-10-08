package dev.indian.snowball.service;

import dev.indian.snowball.model.dto.TradingSignal;
import dev.indian.snowball.model.entity.StrategyEntity;
import dev.indian.snowball.model.strategy.TradingStrategy;
import dev.indian.snowball.rule.TradingStrategyRuleParser;
import dev.indian.snowball.util.TA4JUtils;
import dev.indian.snowball.repository.StrategyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ta4j.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.backtest.BarSeriesManager;

import java.util.List;

@Service
public class BacktestService {
    @Autowired
    private StrategyRepository strategyRepository;
    @Autowired
    private TradingStrategyRuleParser ruleParser;
    @Autowired
    private ObjectMapper objectMapper;

    // Dummy Method, to be completed
    public String runBacktest(Long strategyId) {
        // 1. Fetch strategy
        StrategyEntity entity = strategyRepository.findById(strategyId).orElse(null);
        if (entity == null) {
            return "Strategy not found.";
        }
        // 2. Parse rulesJson
        TradingStrategy tradingStrategy;
        try {
            tradingStrategy = objectMapper.readValue(entity.getRulesJson(), TradingStrategy.class);
        } catch (Exception e) {
            return "Error parsing strategy rules: " + e.getMessage();
        }
        // 3. Load historical data (stubbed)
        List<dev.indian.snowball.model.kite.HistoricalData> historicalData = loadHistoricalDataStub();
        if (historicalData.isEmpty()) {
            return "No historical data available.";
        }
        // 4. Build BarSeries
        BarSeries series = TA4JUtils.createBarSeries(entity.getName(), historicalData);
        // 5. Parse buy/sell rules
        Rule buyRule = ruleParser.parseBuy(tradingStrategy, series);
        Rule sellRule = ruleParser.parseSell(tradingStrategy, series);
        Strategy ta4jStrategy = new BaseStrategy(buyRule, sellRule);
        // 6. Manually simulate trades
        BarSeriesManager manager = new BarSeriesManager(series);
        TradingRecord tradingRecord = manager.run(ta4jStrategy);
        //TradingSignal signal = TA4JUtils.getTradingSignal(series, tradingRecord, ta4jStrategy, instrument, quotes);
        int tradeCount = tradingRecord.getTrades().size();
        double profit = 0.0;
        int winningTrades = 0;

        double winRate = tradeCount > 0 ? (winningTrades * 100.0 / tradeCount) : 0.0;
        return String.format("Backtest results for strategy '%s':\nProfit: %.2f\nTrades: %d\nWin Rate: %.2f%%", entity.getName(), profit, tradeCount, winRate);
    }

    // Stub for loading historical data
    private List<dev.indian.snowball.model.kite.HistoricalData> loadHistoricalDataStub() {
        // TODO: Replace with actual historical data loading logic
        return List.of();
    }
}
