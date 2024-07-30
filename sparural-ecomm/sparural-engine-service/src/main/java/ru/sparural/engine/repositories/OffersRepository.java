package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxOffers;
import ru.sparural.engine.entity.Offer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OffersRepository {
    Optional<Offer> saveOrUpdate(Offer entity);

    void saveLoymaxOffer(Long id, Long loymaxId);

    Optional<ru.sparural.engine.entity.LoymaxOffers> existOffer(Long loymaxId);

    Optional<Offer> get(Long id);

    List<LoymaxOffers> getAllByLoymaxIds(Set<Long> keySet);

    List<Offer> batchSaveOrUpdateOffers(List<Offer> entity);
}
