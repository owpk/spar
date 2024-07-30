package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.main.CounterOfferDto;
import ru.sparural.tables.pojos.OffersCounters;

import java.util.List;
import java.util.Optional;

public interface OffersCounterService {
    List<OffersCounters> index(Integer offset, Integer limit);

    OffersCounters get(Long id);

    OffersCounters create(OffersCounters map);

    OffersCounters update(Long id, OffersCounters map);

    Boolean delete(Long id);

    Optional<CounterOfferDto> getCounterOfferData(Long id);

    void importAndBindForUser(Long userId, Integer loymaxCounterId, Long counterId);
}
