package dev.indian.snowball.model.kite;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HistoricalData {
    private long instrumentToken;
    private String timeStamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
    private long oi;
    private List<HistoricalData> dataArrayList = new ArrayList<>();

    public static HistoricalData toHistoricalData(com.zerodhatech.models.HistoricalData historicalData, Long instrumentToken) {
        HistoricalData kiteHistoricalData = new HistoricalData();
        kiteHistoricalData.setInstrumentToken(instrumentToken);
        kiteHistoricalData.setOpen(historicalData.open);
        kiteHistoricalData.setHigh(historicalData.high);
        kiteHistoricalData.setLow(historicalData.low);
        kiteHistoricalData.setClose(historicalData.close);
        kiteHistoricalData.setVolume(historicalData.volume);
        kiteHistoricalData.setOi(historicalData.oi);
        kiteHistoricalData.setTimeStamp(historicalData.timeStamp);
        if (historicalData.dataArrayList != null) {
            kiteHistoricalData.setDataArrayList(
                    historicalData.dataArrayList.stream()
                            .map(e -> toHistoricalData(e, instrumentToken))
                            .toList()
            );
        }

        return kiteHistoricalData;
    }
}