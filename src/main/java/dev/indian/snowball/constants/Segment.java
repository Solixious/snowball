package dev.indian.snowball.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum Segment {
    EQUITY("equity"),
    COMMODITY("commodity");

    private final String value;
}

