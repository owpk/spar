package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.main.OfferDto;
import ru.sparural.engine.entity.LoymaxOffers;
import ru.sparural.engine.entity.Offer;

import java.util.List;
import java.util.Set;

public interface OfferService {
    OfferDto createDto(Offer entity);

    Offer createEntity(OfferDto dto);

    Offer saveOrUpdateOffer(Offer entity);

    List<Offer> batchSaveOrUpdateOffers(List<Offer> entity);

    void createLoymaxOffer(Long id, Long loymaxId);

    LoymaxOffers existLoymaxOffer(Long loymaxId);

    List<LoymaxOffers> getAllByFilter(Set<Long> keySet);
}
