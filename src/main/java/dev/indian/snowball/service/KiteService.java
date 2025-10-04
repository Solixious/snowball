package dev.indian.snowball.service;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.kiteconnect.utils.Constants;
import com.zerodhatech.models.GTT;
import com.zerodhatech.models.GTTParams;
import com.zerodhatech.models.OrderParams;
import com.zerodhatech.models.User;
import dev.indian.snowball.constants.AppConfigKey;
import dev.indian.snowball.constants.Segment;
import dev.indian.snowball.model.auth.KiteAuthentication;
import dev.indian.snowball.model.kite.*;
import dev.indian.snowball.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class KiteService {

    private final KiteConnect kiteConnect;
    private final AppConfigService appConfigService;
    private final InstrumentCacheService instrumentCacheService;

    @Value("${kite.api.key}")
    private String apiKey;

    @Value("${kite.api.secret}")
    private String apiSecret;

    @Value("${kite.user.id}")
    private String userId;

    public String getLoginUrl() {
        return kiteConnect.getLoginURL();
    }

    public KiteAuthentication generateKiteSession(final String requestToken) {
        try {
            User user = kiteConnect.generateSession(requestToken, apiSecret);
            kiteConnect.setAccessToken(user.accessToken);
            kiteConnect.setPublicToken(user.publicToken);
            kiteConnect.setSessionExpiryHook(this::logout);

            appConfigService.setConfigValue(AppConfigKey.ACCESS_TOKEN, user.accessToken);
            appConfigService.setConfigValue(AppConfigKey.PUBLIC_TOKEN, user.publicToken);
            appConfigService.setConfigValue(AppConfigKey.LOGIN_TIME, java.time.Instant.now().toString());

            return new KiteAuthentication(userId, user.shortName, apiKey, user.accessToken, user.publicToken);

        } catch (Exception | KiteException e) {
            throw new RuntimeException("Failed to generate session", e);
        }
    }

    public KiteAuthentication resumeKiteSession() {
        String accessToken = appConfigService.getConfigValue(AppConfigKey.ACCESS_TOKEN).orElse(null);
        String publicToken = appConfigService.getConfigValue(AppConfigKey.PUBLIC_TOKEN).orElse(null);
        if (accessToken == null || publicToken == null) {
            return null;
        }
        kiteConnect.setAccessToken(accessToken);
        kiteConnect.setPublicToken(publicToken);
        return new KiteAuthentication(userId, null, apiKey, accessToken, publicToken);
    }

    public void logout() {
        try {
            kiteConnect.logout();
            SecurityContextHolder.clearContext();
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to logout", e);
        }
    }

    public HistoricalData getHistoricalData(LocalDateTime fromDate, LocalDateTime toDate, String instrumentToken, String interval, boolean continuous, boolean oi) {
        try {
            return HistoricalData.toHistoricalData(kiteConnect.getHistoricalData(toDate(fromDate), toDate(toDate), instrumentToken, interval, continuous, oi), Long.parseLong(instrumentToken));
        } catch (KiteException | IOException ex) {
            throw new RuntimeException("Failed to fetch historical data", ex);
        }
    }

    public List<Holding> getHoldings() {
        try {
            return kiteConnect.getHoldings()
                    .stream()
                    .map(Holding::toHolding)
                    .toList();
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to fetch holdings", e);
        }
    }

    public List<Instrument> getInstruments(boolean useCache) {
        if (useCache) {
            String cacheTimeStr = appConfigService.getConfigValue(AppConfigKey.INSTRUMENT_CACHE_TIME).orElse(null);
            // Fetch from cache if within 24 hours
            if (TimeUtil.isWithinDuration(cacheTimeStr, Duration.ofHours(24))) {
                List<Instrument> cached = instrumentCacheService.getAllInstruments();
                if (!cached.isEmpty()) {
                    return cached;
                }
            }
        }
        try {
            List<Instrument> instruments = kiteConnect.getInstruments()
                    .stream()
                    .map(Instrument::toInstrument)
                    .toList();
            CompletableFuture.runAsync(() -> {
                instrumentCacheService.saveAllInstruments(instruments);
                appConfigService.setConfigValue(AppConfigKey.INSTRUMENT_CACHE_TIME, java.time.Instant.now().toString());
            });
            return instruments;
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to fetch instruments", e);
        }
    }

    public Margin getMargin(final Segment segment) {
        try {
            return Margin.toMargin(kiteConnect.getMargins(segment.getValue()));
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to fetch margin details", e);
        }
    }

    public List<Order> getOrders() {
        try {
            return kiteConnect.getOrders()
                    .stream().map(Order::toOrder).toList();
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to get orders", e);
        }
    }

    public Map<String, Quote> getQuotes(String[] instruments) {
        try {
            return kiteConnect.getQuote(instruments)
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> Quote.toQuote(entry.getValue())
                    ));
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to fetch quotes", e);
        }
    }

    public String placeMarketOrder(String exchange, String tradingSymbol, String transactionType, int quantity, String product) {
        try {
            OrderParams params = new OrderParams();
            params.exchange = exchange;
            params.tradingsymbol = tradingSymbol;
            params.transactionType = transactionType;
            params.orderType = "MARKET";
            params.quantity = quantity;
            params.product = product;
            params.validity = "DAY";
            return kiteConnect.placeOrder(params, "regular").orderId;
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to place market order", e);
        }
    }

    public String placeLimitOrder(String exchange, String tradingSymbol, String transactionType, int quantity, String product, double price) {
        try {
            OrderParams params = new OrderParams();
            params.exchange = exchange;
            params.tradingsymbol = tradingSymbol;
            params.transactionType = transactionType;
            params.orderType = "LIMIT";
            params.price = price;
            params.quantity = quantity;
            params.product = product;
            params.validity = "DAY";
            return kiteConnect.placeOrder(params, "regular").orderId;
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to place limit order", e);
        }
    }

    public String placeStopLossOrder(String exchange, String tradingSymbol, String transactionType, int quantity, String product, double triggerPrice, double limitPrice) {
        try {
            OrderParams params = new OrderParams();
            params.exchange = exchange;
            params.tradingsymbol = tradingSymbol;
            params.transactionType = transactionType;
            params.orderType = limitPrice > 0 ? "SL" : "SL-M"; // SL has limit price, SL-M is market
            if (limitPrice > 0) {
                params.price = limitPrice;
            }
            params.triggerPrice = triggerPrice;
            params.quantity = quantity;
            params.product = product;
            params.validity = "DAY";
            return kiteConnect.placeOrder(params, "regular").orderId;
        } catch (KiteException | IOException e) {
            throw new RuntimeException("Failed to place stop-loss order", e);
        }
    }

    /**
     * Place an OCO (One Cancels Other) GTT for a long position's exit (target + stop-loss).
     * Both legs are SELL LIMIT orders with distinct trigger prices (lower = stop, higher = target).
     * @return GTT id
     */
    public String placeOcoSellGtt(String exchange, String tradingSymbol, double lastPrice, int quantity,
                                  double stopTrigger, double targetTrigger) {
        try {
            if (stopTrigger >= lastPrice || targetTrigger <= lastPrice) {
                throw new IllegalArgumentException("Invalid triggers relative to last price");
            }
            if (stopTrigger >= targetTrigger) {
                throw new IllegalArgumentException("Stop trigger must be lower than target trigger");
            }
            GTTParams gttParams = new GTTParams();
            gttParams.triggerType = Constants.OCO;
            gttParams.exchange = exchange;
            gttParams.tradingsymbol = tradingSymbol;
            gttParams.lastPrice = lastPrice;
            List<Double> triggerPrices = new ArrayList<>();
            triggerPrices.add(stopTrigger);
            triggerPrices.add(targetTrigger);
            gttParams.triggerPrices = triggerPrices;

            // Stop-loss leg
            GTTParams.GTTOrderParams slLeg = gttParams.new GTTOrderParams();
            slLeg.orderType = Constants.ORDER_TYPE_LIMIT; // limit exit
            slLeg.price = stopTrigger; // price equals trigger for simplicity
            slLeg.product = Constants.PRODUCT_CNC;
            slLeg.transactionType = Constants.TRANSACTION_TYPE_SELL;
            slLeg.quantity = quantity;

            // Target leg
            GTTParams.GTTOrderParams targetLeg = gttParams.new GTTOrderParams();
            targetLeg.orderType = Constants.ORDER_TYPE_LIMIT;
            targetLeg.price = targetTrigger;
            targetLeg.product = Constants.PRODUCT_CNC;
            targetLeg.transactionType = Constants.TRANSACTION_TYPE_SELL;
            targetLeg.quantity = quantity;

            List<GTTParams.GTTOrderParams> ordersList = new ArrayList<>();
            ordersList.add(slLeg);
            ordersList.add(targetLeg);
            gttParams.orders = ordersList;

            GTT gtt = kiteConnect.placeGTT(gttParams);
            return String.valueOf(gtt.id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to place OCO GTT", e);
        } catch (KiteException e) {
            throw new RuntimeException(e);
        }
    }

    private Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Timestamp.valueOf(localDateTime);
    }
}
