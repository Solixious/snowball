package dev.indian.snowball.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtil {
    public static boolean isWithinDuration(String isoTimestamp, Duration duration) {
        if (isoTimestamp == null || duration == null) return false;
        try {
            Instant timestamp = Instant.parse(isoTimestamp);
            Instant now = Instant.now();
            return !timestamp.isAfter(now) && Duration.between(timestamp, now).compareTo(duration) < 0;
        } catch (Exception e) {
            return false;
        }
    }
}

