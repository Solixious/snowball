package dev.indian.snowball.service;

import dev.indian.snowball.model.entity.StrategyEntity;
import dev.indian.snowball.model.dto.StrategyDTO;
import dev.indian.snowball.model.dto.StrategyCreateDTO;
import dev.indian.snowball.model.dto.StrategyUpdateDTO;
import dev.indian.snowball.repository.StrategyRepository;
import dev.indian.snowball.model.strategy.TradingStrategy;
import dev.indian.snowball.rule.TradingStrategyRuleParser;
import org.ta4j.core.Rule;
import org.ta4j.core.BarSeries;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final StrategyRepository strategyRepository;
    private final TradingStrategyRuleParser ruleParser;
    private final ObjectMapper objectMapper;

    public List<StrategyDTO> getAllStrategies() {
        return strategyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Optional<StrategyDTO> getStrategyById(Long id) {
        return strategyRepository.findById(id).map(this::toDTO);
    }

    public Optional<StrategyDTO> getStrategyByName(String name) {
        return strategyRepository.findByName(name).map(this::toDTO);
    }

    public StrategyDTO addStrategy(StrategyCreateDTO dto) {
        StrategyEntity entity = new StrategyEntity();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setRulesJson(dto.getRulesJson());
        return toDTO(strategyRepository.save(entity));
    }

    public StrategyDTO updateStrategy(StrategyUpdateDTO dto) {
        StrategyEntity entity = strategyRepository.findById(dto.getId()).orElseThrow();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setRulesJson(dto.getRulesJson());
        return toDTO(strategyRepository.save(entity));
    }

    public void deleteStrategy(Long id) {
        strategyRepository.deleteById(id);
    }

    private StrategyDTO toDTO(StrategyEntity entity) {
        StrategyDTO dto = new StrategyDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setRulesJson(entity.getRulesJson());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public Rule translateStrategy(Long strategyId, BarSeries series) {
        StrategyEntity entity = strategyRepository.findById(strategyId)
                .orElseThrow(() -> new IllegalArgumentException("Strategy not found: " + strategyId));
        try {
            TradingStrategy tradingStrategy = objectMapper.readValue(entity.getRulesJson(), TradingStrategy.class);
            return ruleParser.parse(tradingStrategy, series);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse strategy JSON", e);
        }
    }
}
