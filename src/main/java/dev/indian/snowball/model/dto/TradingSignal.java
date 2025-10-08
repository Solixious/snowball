package dev.indian.snowball.model.dto;

import dev.indian.snowball.constants.SignalType;
import lombok.Data;

@Data
public class TradingSignal {
    private Long instrumentToken;
    private String instrumentName;
    private SignalType signalType;
    private double price;
    private double volume;
    private double tradedValue;
    private String timeStamp;
    private String strategyName;
    private Double stopLossPrice; // optional absolute price for SL
    private Double targetPrice;   // optional absolute price for target
}
