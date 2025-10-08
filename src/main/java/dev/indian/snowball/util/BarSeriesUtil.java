package dev.indian.snowball.util;

import dev.indian.snowball.model.dto.HistoricalBarDTO;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import java.util.List;

/**
 * Utility to convert a list of HistoricalBarDTO to a TA4J BarSeries.
 * Usage:
 *   BarSeries series = BarSeriesUtil.toBarSeries(data, "MySeries");
 */
public class BarSeriesUtil {
    public static BarSeries toBarSeries(List<HistoricalBarDTO> bars, String seriesName) {
        BarSeries series = new BaseBarSeries(seriesName);
        if (bars == null || bars.isEmpty()) return series;
        for (HistoricalBarDTO dto : bars) {
            series.addBar(new BaseBar(
                dto.getTimestamp(),
                DecimalNum.valueOf(dto.getOpen()),
                DecimalNum.valueOf(dto.getHigh()),
                DecimalNum.valueOf(dto.getLow()),
                DecimalNum.valueOf(dto.getClose()),
                DecimalNum.valueOf(dto.getVolume())
            ));
        }
        return series;
    }
}
