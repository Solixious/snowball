package dev.indian.snowball.controller;

import dev.indian.snowball.model.backtest.BacktestReport;
import dev.indian.snowball.model.dto.StrategyDTO;
import dev.indian.snowball.model.dto.WatchlistDisplayDTO;
import dev.indian.snowball.service.BacktestService;
import dev.indian.snowball.service.StrategyService;
import dev.indian.snowball.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TestStrategyController {
    private final StrategyService strategyService;
    private final BacktestService backtestService;
    private final WatchlistService watchlistService;

    @GetMapping("/test-strategy")
    public String showTestPage(Model model) {
        List<StrategyDTO> strategies = strategyService.getAllStrategies();
        List<WatchlistDisplayDTO> watchlist = watchlistService.getAllDisplay();
        model.addAttribute("strategies", strategies);
        // Expose size for display instead of asking user to pick an instrument
        model.addAttribute("watchlistSize", watchlist.size());
        // Defaults
        model.addAttribute("fromDate", LocalDate.now().minusMonths(6));
        model.addAttribute("toDate", LocalDate.now());
        model.addAttribute("initialCapital", 100000);
        model.addAttribute("perTradeCapitalPct", 10);
        return "test-strategy";
    }

    @PostMapping("/test-strategy")
    public String backtestStrategy(@RequestParam("strategyId") Long strategyId,
                                   @RequestParam("fromDate") String fromDate,
                                   @RequestParam("toDate") String toDate,
                                   @RequestParam("initialCapital") Double initialCapital,
                                   @RequestParam("perTradeCapitalPct") Double perTradeCapitalPct,
                                   Model model) {
        List<StrategyDTO> strategies = strategyService.getAllStrategies();
        List<WatchlistDisplayDTO> watchlist = watchlistService.getAllDisplay();
        model.addAttribute("strategies", strategies);
        model.addAttribute("watchlistSize", watchlist.size());
        // Preserve selections
        model.addAttribute("selectedStrategyId", strategyId);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("initialCapital", initialCapital);
        model.addAttribute("perTradeCapitalPct", perTradeCapitalPct);

        BacktestReport report = backtestService.runBacktest(
                strategyId,
                watchlist.stream().map(WatchlistDisplayDTO::getInstrumentToken).toList(),
                LocalDate.parse(fromDate),
                LocalDate.parse(toDate),
                initialCapital,
                perTradeCapitalPct
        );
        model.addAttribute("report", report);
        return "test-strategy";
    }
}
