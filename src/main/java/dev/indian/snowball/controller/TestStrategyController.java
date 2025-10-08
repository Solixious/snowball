package dev.indian.snowball.controller;

import dev.indian.snowball.model.dto.StrategyDTO;
import dev.indian.snowball.service.BacktestService;
import dev.indian.snowball.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TestStrategyController {
    @Autowired
    private StrategyService strategyService;
    @Autowired
    private BacktestService backtestService;

    @GetMapping("/test-strategy")
    public String showTestPage(Model model) {
        List<StrategyDTO> strategies = strategyService.getAllStrategies();
        model.addAttribute("strategies", strategies);
        return "test-strategy";
    }

    @PostMapping("/test-strategy")
    public String backtestStrategy(@RequestParam("strategyId") Long strategyId, Model model) {
        List<StrategyDTO> strategies = strategyService.getAllStrategies();
        model.addAttribute("strategies", strategies);
        String report = backtestService.runBacktest(strategyId);
        model.addAttribute("report", report);
        return "test-strategy";
    }
}
