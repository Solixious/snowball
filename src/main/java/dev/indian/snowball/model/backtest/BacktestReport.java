package dev.indian.snowball.model.backtest;

import lombok.Data;

import java.util.List;

@Data
public class BacktestReport {
    private String strategyName;
    private double startingCapital;
    private double endingCapital;
    private double netProfit;
    private double netReturnPct;
    private int totalTrades;
    private int winningTrades;
    private int losingTrades;
    private double winRatePct;
    private double avgProfitPerTrade;
    private double maxDrawdownPct;
    private double cagrPct;
    private List<BacktestTrade> trades;
    private List<EquityPoint> equityCurve;
}
