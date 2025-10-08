package dev.indian.snowball.util;

import dev.indian.snowball.constants.SignalType;
import dev.indian.snowball.model.dto.TradingSignal;
import dev.indian.snowball.model.kite.HistoricalData;
import dev.indian.snowball.model.kite.Instrument;
import dev.indian.snowball.model.kite.Quote;
import lombok.experimental.UtilityClass;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Utility to convert a list of HistoricalBarDTO to a TA4J BarSeries.
 * Usage:
 * BarSeries series = BarSeriesUtil.toBarSeries(data, "MySeries");
 */
@UtilityClass
public class TA4JUtils {
    public BarSeries createBarSeries(String name, List<HistoricalData> historicalData) {
        BaseBarSeries series = new BaseBarSeriesBuilder()
                .withName(name)
                .build();
        historicalData
                .stream()
                .map(d -> toBar(series, d))
                .forEach(series::addBar);
        return series;
    }

    private Bar toBar(BaseBarSeries series, HistoricalData data) {
        return BaseBar.builder()
                .timePeriod(Duration.ofDays(1))
                .openPrice(DoubleNum.valueOf(data.getOpen()))
                .highPrice(DoubleNum.valueOf(data.getHigh()))
                .lowPrice(DoubleNum.valueOf(data.getLow()))
                .closePrice(DoubleNum.valueOf(data.getClose()))
                .volume(DecimalNum.valueOf(data.getVolume()))
                .endTime(DateUtil.getZonedDateTime(data.getTimeStamp()))
                .build();
    }

    public TradingRecord runStrategy(BarSeries series, Strategy strategy) {
        BarSeriesManager manager = new BarSeriesManager(series);
        return manager.run(strategy);
    }

    public static TradingSignal getTradingSignal(BarSeries series, TradingRecord record, Strategy strategy, Instrument instrument, Map<String, Quote> quotes) {
        int lastIndex = series.getEndIndex();

        boolean shouldBuy = strategy.shouldEnter(lastIndex);
        boolean shouldSell = strategy.shouldExit(lastIndex, record);

        SignalType signalType;
        if (shouldBuy) {
            signalType = SignalType.BUY;
        } else if (shouldSell) {
            signalType = SignalType.SELL;
        } else {
            signalType = SignalType.HOLD;
        }

        double closePrice = quotes.get(instrument.getInstrumentId()).getLastPrice();

        String timeStamp = series.getBar(lastIndex)
                .getEndTime()
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        TradingSignal signal = new TradingSignal();
        signal.setInstrumentToken(instrument.getInstrumentToken());
        signal.setInstrumentName(instrument.getName());
        signal.setSignalType(signalType);
        signal.setPrice(closePrice);
        signal.setVolume(quotes.get(instrument.getInstrumentId()).getVolumeTradedToday());
        signal.setTradedValue(closePrice * quotes.get(instrument.getInstrumentId()).getVolumeTradedToday());
        signal.setTimeStamp(timeStamp);
        signal.setStrategyName(strategy.getName());

        return signal;
    }
}
