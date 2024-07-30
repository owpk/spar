package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Offer;
import ru.sparural.engine.repositories.OffersRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.LoymaxOffers;
import ru.sparural.tables.Offers;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OffersRepositoryImpl implements OffersRepository {

    private final DSLContext dslContext;
    private final Offers table = Offers.OFFERS;
    private final LoymaxOffers loymaxTable = LoymaxOffers.LOYMAX_OFFERS;


    @Override
    public Optional<Offer> saveOrUpdate(Offer entity) {
        return dslContext
                .insertInto(table)
                .set(table.TITLE, entity.getTitle())
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.BEGIN, entity.getBegin())
                .set(table.END, entity.getEnd())
                .set(table.SHORT_DESCRIPTION, entity.getShortDescription())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict()
                .doUpdate()
                .set(table.DESCRIPTION, entity.getDescription())
                .set(table.BEGIN, entity.getBegin())
                .set(table.END, entity.getEnd())
                .set(table.SHORT_DESCRIPTION, entity.getShortDescription())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Offer.class);
    }

    @Override
    public void saveLoymaxOffer(Long id, Long loymaxId) {
        dslContext
                .insertInto(loymaxTable)
                .set(loymaxTable.LOYMAX_OFFER_ID, loymaxId)
                .set(loymaxTable.OFFER_ID, id)
                .set(loymaxTable.CREATED_AT, TimeHelper.currentTime())
                .onConflict(loymaxTable.LOYMAX_OFFER_ID, loymaxTable.OFFER_ID)
                .doNothing()
                .execute();
    }

    @Override
    public Optional<ru.sparural.engine.entity.LoymaxOffers> existOffer(Long loymaxId) {
        return dslContext
                .selectFrom(loymaxTable)
                .where(loymaxTable.LOYMAX_OFFER_ID.eq(loymaxId))
                .fetchOptionalInto(ru.sparural.engine.entity.LoymaxOffers.class);
    }

    @Override
    public Optional<Offer> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(Offer.class);
    }

    @Override
    public List<ru.sparural.engine.entity.LoymaxOffers> getAllByLoymaxIds(Set<Long> keySet) {
        return dslContext.selectFrom(loymaxTable).where(loymaxTable.LOYMAX_OFFER_ID.in(keySet))
                .fetchInto(ru.sparural.engine.entity.LoymaxOffers.class);
    }

    @Override
    public List<Offer> batchSaveOrUpdateOffers(List<Offer> entities) {
        var insert = dslContext
                .insertInto(table,
                        table.BEGIN,
                        table.DESCRIPTION,
                        table.END,
                        table.SHORT_DESCRIPTION,
                        table.TITLE,
                        table.CREATED_AT);
        for (var entity : entities) {
            insert = insert.values(
                    entity.getBegin(),
                    entity.getDescription(),
                    entity.getEnd(),
                    entity.getShortDescription(),
                    entity.getTitle(),
                    TimeHelper.currentTime());
        }
        return insert
                .onConflict()
                .doNothing()
                .returning()
                .fetch()
                .into(Offer.class);
    }
}
