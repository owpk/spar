package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.main.CounterOfferDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.providers.OffersProvider;
import ru.sparural.engine.repositories.OffersCountersRepository;
import ru.sparural.engine.services.OfferService;
import ru.sparural.engine.services.OffersCounterService;
import ru.sparural.engine.services.OffersCounterUserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.tables.pojos.OffersCounters;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OffersCounterServiceImpl implements OffersCounterService {

    private final OffersCountersRepository offersCountersRepository;
    private final OfferService offerService;
    private final LoymaxService loymaxService;
    private final OffersProvider offersProvider;
    private final OffersCounterUserService offersCounterUserService;

    @Override
    public List<OffersCounters> index(Integer offset, Integer limit) {
        return offersCountersRepository.index(offset, limit);
    }

    @Override
    public OffersCounters get(Long id) {
        return offersCountersRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found with id: " + id));
    }

    @Override
    public OffersCounters create(OffersCounters data) {
        var longValue = Optional.of(data.getLoymaxOfferId())
                .map(x -> {
                    try {
                        return Long.valueOf(x);
                    } catch (Exception e) {
                        throw new RuntimeException("offer counter's loymax offer id must be a number value");
                    }
                }).get();
        if (offerService.existLoymaxOffer(longValue) == null)
            offersProvider.fetchAndSaveOfferByLoymaxOfferId(longValue);
        return offersCountersRepository.saveOrUpdate(data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create counter: " + data));
    }

    @Override
    public OffersCounters update(Long id, OffersCounters data) {
        return offersCountersRepository.update(id, data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update counter: " + data));
    }

    @Override
    public Boolean delete(Long id) {
        return offersCountersRepository.delete(id);
    }

    @Override
    public Optional<CounterOfferDto> getCounterOfferData(Long loymaxOfferId) {
        return offersCountersRepository.getCounterOfferData(loymaxOfferId);
    }

    @Override
    public void importAndBindForUser(Long userId, Integer loymaxCounterId, Long counterId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        var counterInfo = loymaxService.counterInfo(Long.valueOf(loymaxCounterId), loymaxUser.getLoymaxUserId());
        if (!counterInfo.isEmpty()) {
            var value = counterInfo.get(0).getValue();
            offersCounterUserService.bindOfferToUser(Integer.valueOf(value), userId, counterId);
        }
    }
}
