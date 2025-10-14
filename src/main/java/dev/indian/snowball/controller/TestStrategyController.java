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
        model.addAttribute("watchlist", watchlist);
        // Defaults
        model.addAttribute("fromDate", LocalDate.now().minusMonths(6));
        model.addAttribute("toDate", LocalDate.now());
        return "test-strategy";
    }

    @PostMapping("/test-strategy")
    public String backtestStrategy(@RequestParam("strategyId") Long strategyId,
                                   @RequestParam("instrumentToken") String instrumentToken,
                                   @RequestParam("fromDate") String fromDate,
                                   @RequestParam("toDate") String toDate,
                                   Model model) {
        List<StrategyDTO> strategies = strategyService.getAllStrategies();
        List<WatchlistDisplayDTO> watchlist = watchlistService.getAllDisplay();
        model.addAttribute("strategies", strategies);
        model.addAttribute("watchlist", watchlist);
        // Preserve selections
        model.addAttribute("selectedStrategyId", strategyId);
        model.addAttribute("selectedInstrumentToken", instrumentToken);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        BacktestReport report = backtestService.runBacktest(strategyId, List.of(instrumentToken), LocalDate.parse(fromDate), LocalDate.parse(toDate), 0d, 0d);
        model.addAttribute("report", report);
        return "test-strategy";
    }
}
