package dev.indian.snowball.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class TokenUtil {

    public static boolean isLastLoginToday(String loginTime) {
        if (loginTime == null) return false;
        try {
            Instant loginInstant = Instant.parse(loginTime);
            LocalDate loginDate = loginInstant.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now(ZoneId.systemDefault());
            return loginDate.equals(today);
        } catch (Exception e) {
            return false;
        }
    }
}

