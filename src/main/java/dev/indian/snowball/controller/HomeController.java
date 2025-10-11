package dev.indian.snowball.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public String index(Model model) {
        // Placeholder: Replace with actual service/repository calls
        List<Map<String, Object>> holdings = getHoldingsStub();
        List<Map<String, Object>> activeOrders = getActiveOrdersStub();
        boolean marketOpen = isMarketOpenStub();
        model.addAttribute("holdings", holdings);
        model.addAttribute("activeOrders", activeOrders);
        model.addAttribute("marketOpen", marketOpen);
        return "index";
    }

    // Dummy data for holdings
    private List<Map<String, Object>> getHoldingsStub() {
        return List.of(
            Map.of("symbol", "RELIANCE", "quantity", 10, "avgPrice", 2500.0, "currentPrice", 2550.0),
            Map.of("symbol", "TCS", "quantity", 5, "avgPrice", 3500.0, "currentPrice", 3525.0)
        );
    }

    // Dummy data for active orders
    private List<Map<String, Object>> getActiveOrdersStub() {
        return List.of(
            Map.of("id", "ORD123", "symbol", "INFY", "type", "BUY", "quantity", 20, "status", "OPEN"),
            Map.of("id", "ORD124", "symbol", "HDFCBANK", "type", "SELL", "quantity", 15, "status", "OPEN")
        );
    }

    // Dummy market status
    private boolean isMarketOpenStub() {
        // TODO: Replace with actual market status logic
        return true;
    }
}
