package dev.indian.snowball.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class WatchlistDisplayDTO {
    private Long id;
    private String instrumentToken;
    private String name;
    private String tradingSymbol;
    private LocalDateTime addedAt;
}

