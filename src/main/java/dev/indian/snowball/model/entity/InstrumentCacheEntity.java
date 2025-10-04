package dev.indian.snowball.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "instrument_cache")
public class InstrumentCacheEntity {
    @Id
    @Column(name = "instrument_token")
    private Long instrumentToken;

    @Column(name = "trading_symbol", nullable = false, length = 100)
    private String tradingSymbol;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "instrument_type", length = 50)
    private String instrumentType;

    @Column(name = "segment", length = 50)
    private String segment;

    @Column(name = "exchange", length = 50)
    private String exchange;
}

