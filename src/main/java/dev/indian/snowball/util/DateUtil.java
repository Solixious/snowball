package dev.indian.snowball.util;

import dev.indian.snowball.constants.Zone;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

@UtilityClass
public class DateUtil {
    public Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Timestamp.valueOf(localDateTime);
    }

    public static ZonedDateTime getZonedDateTime(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp is null or empty");
        }

        try {
            // Handle timezone offset format without colon (e.g., +0530)
            if (timestamp.matches(".*[+-]\\d{4}$")) {
                timestamp = timestamp.replaceFirst("([+-]\\d{2})(\\d{2})$", "$1:$2");
                return Instant.parse(timestamp).atZone(Zone.INDIA);
            }

            // Handle format without timezone (e.g., 2025-05-25T00:00)
            if (timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
                return LocalDateTime.parse(timestamp)
                        .atZone(Zone.INDIA);
            }

            // Handle microsecond precision
            if (timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+")) {
                return LocalDateTime.parse(timestamp)
                        .atZone(Zone.INDIA);
            }

            // Standard case
            return Instant.parse(timestamp).atZone(Zone.INDIA);
        } catch (DateTimeParseException e) {
            // Fallback to more flexible parsing
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd['T'HH:mm[:ss][.SSS]][XXX][Z]");
            return ZonedDateTime.parse(timestamp, formatter);
        }
    }
}
