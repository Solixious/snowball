package dev.indian.snowball.service;

import org.springframework.stereotype.Service;

@Service
public class BacktestService {
    public String runBacktest(Long strategyId) {
        // TODO: Implement Ta4J backtest logic here
        // Load strategy by ID, parse rulesJson, create Ta4J Strategy
        // Load historical data, run backtest, generate report
        return "Backtest results for strategy ID: " + strategyId + "\nProfit: ...\nTrades: ...";
    }
}

