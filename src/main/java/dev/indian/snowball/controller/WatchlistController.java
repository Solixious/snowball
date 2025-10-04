package dev.indian.snowball.controller;

import dev.indian.snowball.model.dto.WatchlistDisplayDTO;
import dev.indian.snowball.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/watchlist")
@RequiredArgsConstructor
public class WatchlistController {
    private final WatchlistService watchlistService;

    @GetMapping("")
    public String getWatchlist(Model model) {
        List<WatchlistDisplayDTO> watchlist = watchlistService.getAllDisplay();
        model.addAttribute("watchlist", watchlist);
        return "watchlist";
    }

    @PostMapping("/add")
    public String addStock(@RequestParam String instrumentToken) {
        watchlistService.addStock(instrumentToken);
        return "redirect:/watchlist";
    }

    @PostMapping("/remove")
    public String removeStock(@RequestParam Long id) {
        watchlistService.removeStock(id);
        return "redirect:/watchlist";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<InstrumentDTO> searchInstruments(@RequestParam String query) {
        return watchlistService.searchInstruments(query).stream()
                .map(i -> new InstrumentDTO(i.getInstrumentToken(), i.getName(), i.getTradingSymbol()))
                .collect(Collectors.toList());
    }

    public record InstrumentDTO(long instrumentToken, String name, String tradingSymbol) {}
}
