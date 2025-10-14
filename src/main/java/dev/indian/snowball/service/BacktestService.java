package dev.indian.snowball.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.indian.snowball.constants.Zone;
import dev.indian.snowball.model.backtest.*;
import dev.indian.snowball.model.dto.StrategyDTO;
import dev.indian.snowball.model.kite.HistoricalData;
import dev.indian.snowball.model.kite.Instrument;
import dev.indian.snowball.model.strategy.TradingStrategy;
import dev.indian.snowball.rule.TradingStrategyRuleParser;
import dev.indian.snowball.util.TA4JUtils;
import dev.indian.snowball.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BacktestService {
    private final StrategyService strategyService;
    private final TradingStrategyRuleParser ruleParser;
    private final KiteService kiteService;
    private final ObjectMapper objectMapper;
    private final WatchlistRepository watchlistRepository;

    public static final double DEFAULT_INITIAL_CAPITAL = 100000.0;
    public static final double DEFAULT_PER_TRADE_PCT = 0.1;
    public static final double MAX_PRICE = 1000000.0;
    public static final double MIN_PRICE = 0.01;

    // Dummy Method, to be completed
    public String runBacktest(Long strategyId, String instrumentToken, String fromDateStr, String toDateStr) {
        // 1. Fetch strategy using StrategyService
        var strategyOpt = strategyService.getStrategyById(strategyId);
        if (strategyOpt.isEmpty()) {
            return "Strategy not found.";
        }
        StrategyDTO strategyDTO = strategyOpt.get();
        // 2 Parse dates
        LocalDate fromDate;
        LocalDate toDate;
        try {
            fromDate = LocalDate.parse(fromDateStr);
            toDate = LocalDate.parse(toDateStr);
        } catch (Exception e) {
            return "Invalid date format. Use YYYY-MM-DD.";
        }
        if (toDate.isBefore(fromDate)) {
            return "'toDate' cannot be before 'fromDate'.";
        }
        if (instrumentToken == null || instrumentToken.isBlank()) {
            return "Instrument token is required.";
        }
        // 3. Load historical data (validated against watchlist)
        List<dev.indian.snowball.model.kite.HistoricalData> historicalData = loadHistoricalDataStub(instrumentToken, fromDate, toDate);
        if (historicalData.isEmpty()) {
            return "No historical data available.";
        }
        // 4. Build BarSeries
        BarSeries series = TA4JUtils.createBarSeries(strategyDTO.getName(), historicalData);
        // 5. Parse buy/sell rules
        Strategy ta4jStrategy = getBaseStrategy(strategyId, series);
        if (ta4jStrategy == null) {
            return "Failed to build TA4J strategy.";
        }
        // 6. Manually simulate trades
        BarSeriesManager manager = new BarSeriesManager(series);
        TradingRecord tradingRecord = manager.run(ta4jStrategy);
        int tradeCount = tradingRecord.getTrades().size();
        double profit = 0.0;
        int winningTrades = 0;

        double winRate = tradeCount > 0 ? (winningTrades * 100.0 / tradeCount) : 0.0;
        return String.format("Backtest results for strategy '%s':\nProfit: %.2f\nTrades: %d\nWin Rate: %.2f%%", strategyDTO.getName(), profit, tradeCount, winRate);
    }

    /**
     * Runs a backtest for the given strategy and instruments over the specified date range.
     * Returns a detailed BacktestReport with trades, equity curve, and performance metrics.
     */
    public BacktestReport runBacktest(Long strategyId,
                                      List<String> instrumentTokens,
                                      LocalDate fromDate,
                                      LocalDate toDate,
                                      Double initialCapitalOpt,
                                      Double perTradeCapitalPctOpt) {
        List<Instrument> instruments = instrumentTokens.stream().map(kiteService::getInstrumentByToken)
                .filter(Objects::nonNull).toList();
        double initialCapital = (initialCapitalOpt == null || initialCapitalOpt <= 0) ? DEFAULT_INITIAL_CAPITAL : initialCapitalOpt;
        double perTradePct = (perTradeCapitalPctOpt == null || perTradeCapitalPctOpt <= 0) ? DEFAULT_PER_TRADE_PCT : perTradeCapitalPctOpt;
        if (perTradePct > 1.0) {
            perTradePct = perTradePct / 100.0;
        }
        perTradePct = Math.min(Math.max(perTradePct, 0.0001), 1.0);
        double[] cashHolder = new double[]{initialCapital};
        if (instruments == null || instruments.isEmpty()) {
            return getEmptyReport(initialCapital);
        }
        List<BacktestTrade> trades = new ArrayList<>();
        Map<Long, OpenPosition> openPositions = new HashMap<>();
        Set<LocalDate> allDates = new TreeSet<>();
        Map<Long, Map<LocalDate, Double>> closePriceCache = new HashMap<>();
        for (Instrument instrument : instruments) {
            List<HistoricalData> data = fetchValidHistoricalData(instrument, fromDate, toDate);
            if (data.isEmpty()) continue;
            Map<LocalDate, Double> dateClose = data.stream().collect(Collectors.toMap(
                    d -> LocalDate.parse(d.getTimeStamp().substring(0, 10)), HistoricalData::getClose,
                    (a, b) -> b, TreeMap::new));
            closePriceCache.put(instrument.getInstrumentToken(), dateClose);
            Strategy ta4jStrategy = getBaseStrategy(strategyId, TA4JUtils.createBarSeries(instrument.getTradingSymbol(), data));
            if (ta4jStrategy == null) continue;
            trades.addAll(simulateTradesForInstrument(instrument, data, ta4jStrategy, fromDate, toDate, perTradePct, openPositions, allDates, cashHolder));
        }
        List<BacktestTrade> sortedTrades = trades.stream()
                .sorted(Comparator.comparing(BacktestTrade::getExitDate, Comparator.nullsLast(LocalDate::compareTo)))
                .toList();
        List<EquityPoint> equityCurve = buildEquityCurve(sortedTrades, initialCapital, closePriceCache);
        return buildBacktestReport(strategyId, initialCapital, sortedTrades, equityCurve, fromDate, toDate);
    }

    /**
     * Fetches and validates historical data for an instrument.
     */
    private List<HistoricalData> fetchValidHistoricalData(Instrument instrument, LocalDate fromDate, LocalDate toDate) {
        List<HistoricalData> data = kiteService.getHistoricalData(
                fromDate.atStartOfDay(Zone.INDIA).toLocalDateTime(),
                toDate.plusDays(1).atStartOfDay(Zone.INDIA).toLocalDateTime(),
                String.valueOf(instrument.getInstrumentToken()),
                "day",
                false, false).getDataArrayList();
        if (data.isEmpty()) return Collections.emptyList();
        if (!data.stream().allMatch(this::isValidPrice)) return Collections.emptyList();
        return data;
    }

    /**
     * Simulates trades for a single instrument and returns completed trades and open position.
     */
    private List<BacktestTrade> simulateTradesForInstrument(Instrument instrument, List<HistoricalData> data, Strategy ta4jStrategy,
                                                            LocalDate fromDate, LocalDate toDate, double perTradePct, Map<Long, OpenPosition> openPositions,
                                                            Set<LocalDate> allDates, double[] cashHolder) {
        List<BacktestTrade> trades = new ArrayList<>();
        BarSeries series = TA4JUtils.createBarSeries(instrument.getTradingSymbol(), data);
        for (int i = 0; i <= series.getEndIndex(); i++) {
            LocalDate barDate = series.getBar(i).getEndTime().toLocalDate();
            if (barDate.isBefore(fromDate) || barDate.isAfter(toDate)) continue;
            allDates.add(barDate);
            double closePrice = series.getBar(i).getClosePrice().doubleValue();
            OpenPosition pos = openPositions.get(instrument.getInstrumentToken());
            // Check for exit condition
            if (pos != null && ta4jStrategy.shouldExit(i)) {
                if (pos.entryPrice <= 0 || closePrice <= 0) {
                    openPositions.remove(instrument.getInstrumentToken());
                } else {
                    double profit = (closePrice - pos.entryPrice) * pos.quantity;
                    cashHolder[0] += pos.quantity * closePrice;
                    // Record completed trade
                    BacktestTrade trade = new BacktestTrade();
                    trade.setInstrumentToken(instrument.getInstrumentToken());
                    trade.setInstrumentName(instrument.getName());
                    trade.setEntryIndex(pos.entryIndex);
                    trade.setExitIndex(i);
                    trade.setEntryDate(pos.entryDate);
                    trade.setExitDate(barDate);
                    trade.setEntryPrice(pos.entryPrice);
                    trade.setExitPrice(closePrice);
                    trade.setQuantity(pos.quantity);
                    trade.setProfit(profit);
                    trade.setReturnPct((profit) / (pos.entryPrice * pos.quantity) * 100.0);
                    trades.add(trade);
                    openPositions.remove(instrument.getInstrumentToken());
                }
            }
            // Check for entry condition (only if no open position)
            if (!openPositions.containsKey(instrument.getInstrumentToken()) && ta4jStrategy.shouldEnter(i)) {
                double alloc = cashHolder[0] * perTradePct;
                int qty = (int) Math.floor(alloc / closePrice);
                if (qty <= 0) continue;
                cashHolder[0] -= qty * closePrice;
                // Open new position
                OpenPosition np = new OpenPosition();
                np.instrumentToken = instrument.getInstrumentToken();
                np.instrumentName = instrument.getName();
                np.entryIndex = i;
                np.entryDate = barDate;
                np.entryPrice = closePrice;
                np.quantity = qty;
                openPositions.put(instrument.getInstrumentToken(), np);
            }
        }
        // If position still open at end, close at last bar
        OpenPosition remaining = openPositions.remove(instrument.getInstrumentToken());
        if (remaining != null) {
            int lastIndex = series.getEndIndex();
            double lastPrice = series.getBar(lastIndex).getClosePrice().doubleValue();
            LocalDate barDate = series.getBar(lastIndex).getEndTime().toLocalDate();
            remaining.exitIndex = lastIndex;
            remaining.exitPrice = lastPrice;
            remaining.exitDate = barDate;
            if (remaining.entryPrice > 0 && remaining.exitPrice > 0) {
                double profit = (remaining.exitPrice - remaining.entryPrice) * remaining.quantity;
                cashHolder[0] += remaining.quantity * remaining.exitPrice;
                BacktestTrade trade = new BacktestTrade();
                trade.setInstrumentToken(instrument.getInstrumentToken());
                trade.setInstrumentName(instrument.getName());
                trade.setEntryIndex(remaining.entryIndex);
                trade.setExitIndex(lastIndex);
                trade.setEntryDate(remaining.entryDate);
                trade.setExitDate(barDate);
                trade.setEntryPrice(remaining.entryPrice);
                trade.setExitPrice(remaining.exitPrice);
                trade.setQuantity(remaining.quantity);
                trade.setProfit(profit);
                trade.setReturnPct((profit) / (remaining.entryPrice * remaining.quantity) * 100.0);
                trades.add(trade);
            }
        }
        return trades;
    }

    /**
     * Builds the equity curve from trades and mark-to-market positions.
     */
    private List<EquityPoint> buildEquityCurve(List<BacktestTrade> trades, double initialCapital, Map<Long, Map<LocalDate, Double>> closePriceCache) {
        List<EquityPoint> equityCurve = new ArrayList<>();
        double runningCash = initialCapital;
        Map<Long, PositionSlice> active = new HashMap<>();
        Set<LocalDate> allDates = new TreeSet<>();
        for (BacktestTrade t : trades) {
            allDates.add(t.getEntryDate());
            allDates.add(t.getExitDate());
        }
        List<LocalDate> orderedDates = new ArrayList<>(allDates);
        Collections.sort(orderedDates);
        for (LocalDate d : orderedDates) {
            for (BacktestTrade t : trades) {
                if (t.getEntryDate().equals(d)) {
                    runningCash -= t.getEntryPrice() * t.getQuantity();
                    active.put(t.getInstrumentToken(), new PositionSlice(t.getInstrumentToken(), t.getEntryPrice(), t.getQuantity()));
                }
            }
            for (BacktestTrade t : trades) {
                if (t.getExitDate().equals(d)) {
                    runningCash += t.getExitPrice() * t.getQuantity();
                    active.remove(t.getInstrumentToken());
                }
            }
            double mtm = 0.0;
            for (PositionSlice ps : active.values()) {
                Map<LocalDate, Double> dc = closePriceCache.get(ps.instrumentToken());
                Double cp = dc != null ? dc.get(d) : null;
                double price = cp != null ? cp : ps.entryPrice();
                mtm += price * ps.quantity();
            }
            equityCurve.add(new EquityPoint(d, runningCash + mtm));
        }
        return equityCurve;
    }

    /**
     * Builds the final BacktestReport from trades and equity curve.
     */
    private BacktestReport buildBacktestReport(Long strategyId, double initialCapital, List<BacktestTrade> trades, List<EquityPoint> equityCurve, LocalDate fromDate, LocalDate toDate) {
        double profitSum = trades.stream().mapToDouble(BacktestTrade::getProfit).sum();
        double endingCapital = initialCapital + profitSum;
        if (!equityCurve.isEmpty()) {
            equityCurve.get(equityCurve.size() - 1).setEquity(endingCapital);
        }
        int wins = (int) trades.stream().filter(t -> t.getProfit() > 0).count();
        int losses = (int) trades.stream().filter(t -> t.getProfit() < 0).count();
        double netProfit = endingCapital - initialCapital;
        double netReturnPct = initialCapital > 0 ? (endingCapital / initialCapital - 1) * 100.0 : 0.0;
        double avgProfit = trades.isEmpty() ? 0 : trades.stream().mapToDouble(BacktestTrade::getProfit).average().orElse(0);
        double maxDrawdownPct = computeMaxDrawdownPct(equityCurve);
        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        double years = days / 365.25;
        double cagr = years > 0 ? (Math.pow(endingCapital / initialCapital, 1 / years) - 1) * 100 : 0;
        BacktestReport report = new BacktestReport();
        report.setStrategyName(strategyId != null ? strategyId.toString() : "");
        report.setStartingCapital(initialCapital);
        report.setEndingCapital(endingCapital);
        report.setNetProfit(netProfit);
        report.setNetReturnPct(netReturnPct);
        report.setTotalTrades(trades.size());
        report.setWinningTrades(wins);
        report.setLosingTrades(losses);
        report.setWinRatePct(trades.isEmpty() ? 0 : (wins * 100.0 / trades.size()));
        report.setAvgProfitPerTrade(avgProfit);
        report.setMaxDrawdownPct(maxDrawdownPct);
        report.setCagrPct(cagr);
        report.setTrades(trades);
        report.setEquityCurve(equityCurve);
        return report;
    }

    /**
     * Loads a strategy by ID and constructs a TA4J BaseStrategy for the given BarSeries.
     * Returns null if strategy not found or rules cannot be parsed.
     */
    public BaseStrategy getBaseStrategy(Long strategyId, BarSeries series) {
        var strategyOpt = strategyService.getStrategyById(strategyId);
        if (strategyOpt.isEmpty()) {
            return null;
        }
        StrategyDTO strategyDTO = strategyOpt.get();
        TradingStrategy tradingStrategy;
        try {
            tradingStrategy = objectMapper.readValue(strategyDTO.getRulesJson(), TradingStrategy.class);
        } catch (Exception e) {
            return null;
        }
        Rule buyRule = ruleParser.parseBuy(tradingStrategy, series);
        Rule sellRule = ruleParser.parseSell(tradingStrategy, series);
        return new BaseStrategy(buyRule, sellRule);
    }

    // Stub for loading historical data
    private List<dev.indian.snowball.model.kite.HistoricalData> loadHistoricalDataStub(String instrumentToken, LocalDate fromDate, LocalDate toDate) {
        // Validate instrument token exists in watchlist
        if (watchlistRepository.findByInstrumentToken(instrumentToken).isEmpty()) {
            return Collections.emptyList();
        }
        try {
            dev.indian.snowball.model.kite.HistoricalData hd = kiteService.getHistoricalData(
                    fromDate.atStartOfDay(Zone.INDIA).toLocalDateTime(),
                    toDate.plusDays(1).atStartOfDay(Zone.INDIA).toLocalDateTime(),
                    instrumentToken,
                    "day",
                    false,
                    false
            );
            if (hd == null || hd.getDataArrayList() == null) {
                return Collections.emptyList();
            }
            List<dev.indian.snowball.model.kite.HistoricalData> list = hd.getDataArrayList();
            // Filter invalid prices
            return list.stream().filter(this::isValidPrice).toList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private BacktestReport getEmptyReport(double initialCapital) {
        BacktestReport report = new BacktestReport();
        report.setStrategyName("");
        report.setStartingCapital(initialCapital);
        report.setEndingCapital(initialCapital);
        report.setNetProfit(0.0);
        report.setNetReturnPct(0.0);
        report.setTotalTrades(0);
        report.setWinningTrades(0);
        report.setLosingTrades(0);
        report.setWinRatePct(0.0);
        report.setAvgProfitPerTrade(0.0);
        report.setMaxDrawdownPct(0.0);
        report.setCagrPct(0.0);
        report.setTrades(Collections.emptyList());
        report.setEquityCurve(Collections.emptyList());
        return report;
    }

    private double computeMaxDrawdownPct(List<EquityPoint> equityCurve) {
        double peak = Double.NEGATIVE_INFINITY;
        double maxDrawdown = 0.0;
        for (EquityPoint pt : equityCurve) {
            if (pt.getEquity() > peak) peak = pt.getEquity();
            double drawdown = peak - pt.getEquity();
            if (drawdown > maxDrawdown) maxDrawdown = drawdown;
        }
        if (peak > 0) {
            return (maxDrawdown / peak) * 100.0;
        }
        return 0.0;
    }

    // Helper to validate price data
    boolean isValidPrice(HistoricalData d) {
        return d.getOpen() > MIN_PRICE && d.getOpen() < MAX_PRICE &&
                d.getHigh() > MIN_PRICE && d.getHigh() < MAX_PRICE &&
                d.getLow() > MIN_PRICE && d.getLow() < MAX_PRICE &&
                d.getClose() > MIN_PRICE && d.getClose() < MAX_PRICE;
    }
}
