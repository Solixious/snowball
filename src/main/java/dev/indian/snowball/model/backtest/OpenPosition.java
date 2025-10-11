package dev.indian.snowball.model.backtest;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OpenPosition {
    public long instrumentToken;
    public String instrumentName;
    public int entryIndex;
    public int exitIndex;
    public LocalDate entryDate;
    public LocalDate exitDate;
    public double entryPrice;
    public double exitPrice;
    public int quantity;
}

