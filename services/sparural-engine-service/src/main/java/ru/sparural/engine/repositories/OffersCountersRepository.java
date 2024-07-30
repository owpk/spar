package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.main.CounterOfferDto;
import ru.sparural.tables.pojos.OffersCounters;

import java.util.List;
import java.util.Optional;

public interface OffersCountersRepository {
    List<OffersCounters> index(Integer offset, Integer limit);

    Optional<OffersCounters> get(Long id);

    Optional<OffersCounters> saveOrUpdate(OffersCounters data);

    Optional<OffersCounters> update(Long id, OffersCounters data);

    Boolean delete(Long id);

    Optional<CounterOfferDto> getCounterOfferData(Long loymaxOfferId);
}
