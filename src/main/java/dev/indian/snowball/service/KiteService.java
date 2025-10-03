package dev.indian.snowball.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.User;
import dev.indian.snowball.model.KiteAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class KiteService {

    private final KiteConnect kiteConnect;

    @Value("${kite.api.key}")
    private String apiKey;

    @Value("${kite.api.secret}")
    private String apiSecret;

    @Value("${kite.user.id}")
    private String userId;

    public String getLoginUrl() {
        return kiteConnect.getLoginURL();
    }

    public KiteAuthentication generateSession(final String requestToken) {
        try {
            User user = kiteConnect.generateSession(requestToken, apiSecret);
            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);
            kiteConnect.setSessionExpiryHook(this::logout);
            return new KiteAuthentication(userId, user.shortName, apiKey, user.accessToken, user.publicToken);

        } catch (Exception | KiteException e) {
            throw new RuntimeException("Failed to generate session", e);
        }
    }

    public void logout() {
        try {
            kiteConnect.logout();
            SecurityContextHolder.clearContext();
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to logout", e);
        }
    }
}
