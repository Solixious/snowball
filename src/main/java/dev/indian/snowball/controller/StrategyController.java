package dev.indian.snowball.controller;

import dev.indian.snowball.model.dto.StrategyCreateDTO;
import dev.indian.snowball.model.dto.StrategyDTO;
import dev.indian.snowball.model.dto.StrategyUpdateDTO;
import dev.indian.snowball.service.StrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/strategies")
public class StrategyController {
    private final StrategyService strategyService;

    @GetMapping
    public String listStrategies(@RequestParam(value = "id", required = false) Long id, Model model) {
        model.addAttribute("strategies", strategyService.getAllStrategies());
        if (id != null) {
            Optional<StrategyDTO> strategy = strategyService.getStrategyById(id);
            model.addAttribute("strategy", strategy.orElse(new StrategyDTO()));
        } else {
            model.addAttribute("strategy", new StrategyDTO());
        }
        return "strategies";
    }

    @PostMapping
    public String saveStrategy(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam String name,
                               @RequestParam String description,
                               @RequestParam String rulesJson) {
        if (id == null || id == 0) {
            StrategyCreateDTO dto = new StrategyCreateDTO();
            dto.setName(name);
            dto.setDescription(description);
            dto.setRulesJson(rulesJson);
            strategyService.addStrategy(dto);
        } else {
            StrategyUpdateDTO dto = new StrategyUpdateDTO();
            dto.setId(id);
            dto.setName(name);
            dto.setDescription(description);
            dto.setRulesJson(rulesJson);
            strategyService.updateStrategy(dto);
        }
        return "redirect:/strategies";
    }

    @GetMapping("/edit")
    public String editStrategy(@RequestParam Long id, Model model) {
        return listStrategies(id, model);
    }

    @PostMapping("/delete")
    public String deleteStrategy(@RequestParam Long id) {
        strategyService.deleteStrategy(id);
        return "redirect:/strategies";
    }
}

