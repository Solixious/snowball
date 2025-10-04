package dev.indian.snowball.model.dto;

import java.time.LocalDateTime;

public class WatchlistDisplayDTO {
    private Long id;
    private String instrumentToken;
    private String name;
    private String tradingSymbol;
    private LocalDateTime addedAt;

    public WatchlistDisplayDTO(Long id, String instrumentToken, String name, String tradingSymbol, LocalDateTime addedAt) {
        this.id = id;
        this.instrumentToken = instrumentToken;
        this.name = name;
        this.tradingSymbol = tradingSymbol;
        this.addedAt = addedAt;
    }

    public Long getId() { return id; }
    public String getInstrumentToken() { return instrumentToken; }
    public String getName() { return name; }
    public String getTradingSymbol() { return tradingSymbol; }
    public LocalDateTime getAddedAt() { return addedAt; }
}

