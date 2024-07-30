package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.LoymaxPersonalOffersEntity;
import ru.sparural.engine.repositories.LoymaxPersonalOfferRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxPersonalOffers;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoymaxPersonalOfferRepositoryImpl implements LoymaxPersonalOfferRepository {
    private final DSLContext dsl;
    private final LoymaxPersonalOffers table = LoymaxPersonalOffers.LOYMAX_PERSONAL_OFFERS;

    @Override
    @Transactional
    public void saveOrUpdate(LoymaxPersonalOffersEntity entity) {
        var result = dsl.insertInto(table)
                .set(table.LOYMAXOFFERID, entity.getLoymaxOfferId())
                .set(table.PERSONALOFFERID, entity.getPersonalOfferId())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.LOYMAXOFFERID, table.PERSONALOFFERID)
                .doNothing()
                .execute();
        log.debug("Inserting loymax personal offer entity, result: " + result);
    }

    @Override
    public Optional<LoymaxPersonalOffersEntity> findByLoymaxOfferId(Long loymaxOfferId) {
        return dsl.select().from(table).where(table.LOYMAXOFFERID.eq(loymaxOfferId))
                .fetchOptionalInto(LoymaxPersonalOffersEntity.class);
    }
}
