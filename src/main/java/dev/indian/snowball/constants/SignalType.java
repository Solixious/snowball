package dev.indian.snowball.constants;

import lombok.Getter;

@Getter
public enum SignalType {
    BUY("BUY"),
    SELL("SELL"),
    HOLD("HOLD");

    private final String type;

    SignalType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
