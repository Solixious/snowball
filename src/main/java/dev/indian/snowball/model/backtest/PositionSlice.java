package dev.indian.snowball.model.backtest;

public class PositionSlice {
    private long instrumentToken;
    private double entryPrice;
    private int quantity;

    public PositionSlice(long instrumentToken, double entryPrice, int quantity) {
        this.instrumentToken = instrumentToken;
        this.entryPrice = entryPrice;
        this.quantity = quantity;
    }

    public long instrumentToken() { return instrumentToken; }
    public double entryPrice() { return entryPrice; }
    public int quantity() { return quantity; }
}

