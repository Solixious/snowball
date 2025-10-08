package dev.indian.snowball.util;

import dev.indian.snowball.model.kite.HistoricalData;
import lombok.experimental.UtilityClass;
import org.ta4j.core.*;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;

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
}
