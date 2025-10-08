package dev.indian.snowball.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalBarDTO {
    private ZonedDateTime timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
}

