package dev.indian.snowball.util;

import dev.indian.snowball.model.kite.HistoricalData;
import lombok.experimental.UtilityClass;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;

import java.time.Duration;
import java.util.List;

/**
 * Utility to convert a list of HistoricalBarDTO to a TA4J BarSeries.
 * Usage:
 * BarSeries series = BarSeriesUtil.toBarSeries(data, "MySeries");
 */
@UtilityClass
public class BarSeriesUtil {
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
        return series.barBuilder()
                .timePeriod(Duration.ofDays(1))
                .openPrice(data.getOpen())
                .highPrice(data.getHigh())
                .lowPrice(data.getLow())
                .closePrice(data.getClose())
                .volume(data.getVolume())
                .endTime(DateUtil.getInstant(data.getTimeStamp()))
                .build();
    }
}
