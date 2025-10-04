package dev.indian.snowball.service;

import dev.indian.snowball.model.dto.WatchlistDisplayDTO;
import dev.indian.snowball.model.entity.WatchlistEntity;
import dev.indian.snowball.model.kite.Instrument;
import dev.indian.snowball.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistService {
    private final WatchlistRepository watchlistRepository;
    private final KiteService kiteService;

    public List<WatchlistEntity> getAll() {
        return watchlistRepository.findAll();
    }

    public void addStock(String instrumentToken) {
        // Add only if not already present
        if (watchlistRepository.findByInstrumentToken(instrumentToken).isEmpty()) {
            watchlistRepository.save(new WatchlistEntity(instrumentToken));
        }
    }

    public void removeStock(Long id) {
        watchlistRepository.deleteById(id);
    }

    public List<Instrument> searchInstruments(String query) {
        return kiteService.getInstruments(true).stream()
                .filter(i -> i.getName() != null && i.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<WatchlistDisplayDTO> getAllDisplay() {
        List<WatchlistEntity> entities = watchlistRepository.findAll();
        List<Instrument> instruments = kiteService.getInstruments(true);
        return entities.stream().map(entity -> {
            Instrument instrument = instruments.stream()
                .filter(i -> String.valueOf(i.getInstrumentToken()).equals(entity.getInstrumentToken()))
                .findFirst().orElse(null);
            String name = instrument != null ? instrument.getName() : "Unknown";
            String tradingSymbol = instrument != null ? instrument.getTradingSymbol() : "";
            return new WatchlistDisplayDTO(
                entity.getId(),
                entity.getInstrumentToken(),
                name,
                tradingSymbol,
                entity.getAddedAt()
            );
        }).toList();
    }
}
