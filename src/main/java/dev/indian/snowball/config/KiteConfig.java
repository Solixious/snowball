package dev.indian.snowball.config;

import com.zerodhatech.kiteconnect.KiteConnect;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KiteConfig {
    @Value("${kite.api.key}")
    private String apiKey;

    @Value("${kite.user.id}")
    private String userId;

    @Bean
    public KiteConnect kiteConnect() {
        KiteConnect kiteConnect = new KiteConnect(apiKey);
        kiteConnect.setUserId(userId);
        return kiteConnect;
    }
}
