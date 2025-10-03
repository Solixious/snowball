package dev.indian.snowball.model.kite;

import lombok.Data;

import java.util.Date;

@Data
public class Instrument {
    private long instrumentToken;
    private long exchangeToken;
    private String tradingSymbol;
    private String name;
    private double lastPrice;
    private double tickSize;
    private String instrumentType;
    private String segment;
    private String exchange;
    private String strike;
    private int lotSize;
    private Date expiry;

    public String getInstrumentId() {
        return getExchange() + ":" + getTradingSymbol();
    }

    public static Instrument toInstrument(com.zerodhatech.models.Instrument instrument) {
        Instrument kiteInstrument = new Instrument();
        kiteInstrument.setInstrumentToken(instrument.instrument_token);
        kiteInstrument.setExchangeToken(instrument.exchange_token);
        kiteInstrument.setTradingSymbol(instrument.tradingsymbol);
        kiteInstrument.setName(instrument.name);
        kiteInstrument.setLastPrice(instrument.last_price);
        kiteInstrument.setTickSize(instrument.tick_size);
        kiteInstrument.setInstrumentType(instrument.instrument_type);
        kiteInstrument.setSegment(instrument.segment);
        kiteInstrument.setExchange(instrument.exchange);
        kiteInstrument.setStrike(instrument.strike);
        kiteInstrument.setLotSize(instrument.lot_size);
        kiteInstrument.setExpiry(instrument.expiry);

        return kiteInstrument;
    }
}