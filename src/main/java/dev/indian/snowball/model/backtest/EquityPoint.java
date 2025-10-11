package dev.indian.snowball.model.backtest;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EquityPoint {
    private LocalDate date;
    private double equity;

    public EquityPoint(LocalDate date, double equity) {
        this.date = date;
        this.equity = equity;
    }
}

