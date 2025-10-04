package dev.indian.snowball.service;

import dev.indian.snowball.model.entity.InstrumentCacheEntity;
import dev.indian.snowball.model.kite.Instrument;
import dev.indian.snowball.repository.InstrumentCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstrumentCacheService {
    private final InstrumentCacheRepository instrumentCacheRepository;

    public List<Instrument> getAllInstruments() {
        return instrumentCacheRepository.findAll().stream()
                .map(this::toInstrument)
                .toList();
    }

    public Optional<Instrument> getInstrumentByToken(Long instrumentToken) {
        return instrumentCacheRepository.findById(instrumentToken)
                .map(this::toInstrument);
    }

    public InstrumentCacheEntity saveInstrument(Instrument instrument) {
        return instrumentCacheRepository.save(getInstrumentEntity(instrument));
    }

    public void saveAllInstruments(List<Instrument> instruments) {
        List<InstrumentCacheEntity> entities = instruments.stream().map(this::getInstrumentEntity).toList();
        instrumentCacheRepository.saveAll(entities);
    }

    public void deleteInstrument(Long instrumentToken) {
        instrumentCacheRepository.deleteById(instrumentToken);
    }

    public void clearCache() {
        instrumentCacheRepository.deleteAll();
    }

    private InstrumentCacheEntity getInstrumentEntity(Instrument instrument) {
        Long token = instrument.getInstrumentToken();
        InstrumentCacheEntity entity = instrumentCacheRepository.findById(token)
                .orElseGet(InstrumentCacheEntity::new);
        entity.setInstrumentToken(token);
        entity.setTradingSymbol(instrument.getTradingSymbol());
        entity.setName(instrument.getName());
        entity.setInstrumentType(instrument.getInstrumentType());
        entity.setSegment(instrument.getSegment());
        entity.setExchange(instrument.getExchange());
        return entity;
    }

    private Instrument toInstrument(InstrumentCacheEntity entity) {
        Instrument instrument = new Instrument();
        instrument.setInstrumentToken(entity.getInstrumentToken());
        instrument.setTradingSymbol(entity.getTradingSymbol());
        instrument.setName(entity.getName());
        instrument.setInstrumentType(entity.getInstrumentType());
        instrument.setSegment(entity.getSegment());
        instrument.setExchange(entity.getExchange());
        return instrument;
    }
}
