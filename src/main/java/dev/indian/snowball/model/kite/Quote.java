package dev.indian.snowball.model.kite;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Quote {
    public double volumeTradedToday;
    public double lastTradedQuantity;
    public Date lastTradedTime;
    public double change;
    public double oi;
    public double sellQuantity;
    public double lastPrice;
    public double buyQuantity;
    public OHLC ohlc;
    public long instrumentToken;
    public Date timestamp;
    public double averagePrice;
    public double oiDayHigh;
    public double oiDayLow;
    public MarketDepth depth;
    public double lowerCircuitLimit;
    public double upperCircuitLimit;

    @Data
    public static class OHLC {
        public double high;
        public double low;
        public double close;
        public double open;
    }

    @Data
    public static class MarketDepth {
        public List<Depth> buy;
        public List<Depth> sell;
    }

    @Data
    public static class Depth {
        public double price;
        public int quantity;
        public int orders;
    }

    public static Quote toQuote(com.zerodhatech.models.Quote quote) {
        Quote kiteQuote = new Quote();
        kiteQuote.setVolumeTradedToday(quote.volumeTradedToday);
        kiteQuote.setLastTradedQuantity(quote.lastTradedQuantity);
        kiteQuote.setLastTradedTime(quote.lastTradedTime);
        kiteQuote.setChange(quote.change);
        kiteQuote.setOi(quote.oi);
        kiteQuote.setSellQuantity(quote.sellQuantity);
        kiteQuote.setLastPrice(quote.lastPrice);
        kiteQuote.setBuyQuantity(quote.buyQuantity);
        kiteQuote.setOhlc(toOHLC(quote.ohlc));
        kiteQuote.setInstrumentToken(quote.instrumentToken);
        kiteQuote.setTimestamp(quote.timestamp);
        kiteQuote.setAveragePrice(quote.averagePrice);
        kiteQuote.setOiDayHigh(quote.oiDayHigh);
        kiteQuote.setOiDayLow(quote.oiDayLow);
        kiteQuote.setDepth(toMarketDepth(quote.depth));
        kiteQuote.setLowerCircuitLimit(quote.lowerCircuitLimit);
        kiteQuote.setUpperCircuitLimit(quote.upperCircuitLimit);

        return kiteQuote;
    }

    private static Quote.OHLC toOHLC(com.zerodhatech.models.OHLC ohlc) {
        Quote.OHLC kiteOhlc = new Quote.OHLC();
        kiteOhlc.setHigh(ohlc.high);
        kiteOhlc.setLow(ohlc.low);
        kiteOhlc.setClose(ohlc.close);
        kiteOhlc.setOpen(ohlc.open);

        return kiteOhlc;
    }

    private static Quote.MarketDepth toMarketDepth(com.zerodhatech.models.MarketDepth marketDepth) {
        if (marketDepth == null) {
            return null;
        }

        Quote.MarketDepth kiteMarketDepth = new Quote.MarketDepth();
        kiteMarketDepth.setBuy(marketDepth.buy.stream().map(Quote::toDepth).toList());
        kiteMarketDepth.setSell(marketDepth.sell.stream().map(Quote::toDepth).toList());

        return kiteMarketDepth;
    }

    private static Quote.Depth toDepth(com.zerodhatech.models.Depth depth) {
        Quote.Depth kiteDepth = new Quote.Depth();
        kiteDepth.setPrice(depth.getPrice());
        kiteDepth.setQuantity(depth.getQuantity());
        kiteDepth.setOrders(depth.getOrders());

        return kiteDepth;
    }
}