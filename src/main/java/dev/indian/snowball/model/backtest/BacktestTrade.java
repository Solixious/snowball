package dev.indian.snowball.model.backtest;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BacktestTrade {
    private long instrumentToken;
    private String instrumentName;
    private int entryIndex;
    private int exitIndex;
    private LocalDate entryDate;
    private LocalDate exitDate;
    private double entryPrice;
    private double exitPrice;
    private int quantity;
    private double profit;
    private double returnPct;
}

