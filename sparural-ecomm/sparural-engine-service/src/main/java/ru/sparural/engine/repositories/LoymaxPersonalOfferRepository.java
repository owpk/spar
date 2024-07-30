package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.LoymaxPersonalOffersEntity;

import java.util.Optional;

public interface LoymaxPersonalOfferRepository {
    void saveOrUpdate(LoymaxPersonalOffersEntity entity);

    Optional<LoymaxPersonalOffersEntity> findByLoymaxOfferId(Long loymaxOfferId);
}

