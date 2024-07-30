package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.main.CounterOfferDto;
import ru.sparural.engine.repositories.OffersCountersRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.OffersCounterUser;
import ru.sparural.tables.pojos.OffersCounters;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OffersCountersRepositoryImpl implements OffersCountersRepository {

    private final DSLContext dslContext;
    private final ru.sparural.tables.OffersCounters table = ru.sparural.tables.OffersCounters.OFFERS_COUNTERS;

    @Override
    public List<OffersCounters> index(Integer offset, Integer limit) {
        var selectStep = dslContext.select().from(table).offset(offset);
        if (!limit.equals(-1))
            return selectStep.limit(limit).fetchInto(OffersCounters.class);
        return selectStep.fetchInto(OffersCounters.class);
    }

    @Override
    public Optional<OffersCounters> get(Long id) {
        return dslContext.select().from(table).where(table.ID.eq(id)).fetchOptionalInto(OffersCounters.class);
    }

    @Override
    public Optional<OffersCounters> saveOrUpdate(OffersCounters data) {
        return dslContext.insertInto(table)
                .set(table.OFFER_ID, data.getOfferId())
                .set(table.LOYMAX_COUNTER_ID, data.getLoymaxCounterId())
                .set(table.IS_PUBLIC, data.getIsPublic())
                .set(table.LOYMAX_OFFER_ID, data.getLoymaxOfferId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.LOYMAX_COUNTER_ID, table.LOYMAX_OFFER_ID)
                .doNothing()
                .returning()
                .fetchOptionalInto(OffersCounters.class);
    }

    @Override
    public Optional<OffersCounters> update(Long id, OffersCounters data) {
        return dslContext.update(table)
                .set(table.OFFER_ID, data.getOfferId())
                .set(table.LOYMAX_COUNTER_ID, data.getLoymaxCounterId())
                .set(table.IS_PUBLIC, data.getIsPublic())
                .set(table.LOYMAX_OFFER_ID, data.getLoymaxOfferId())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(id))
                .returning().fetchOptionalInto(OffersCounters.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table).where(table.ID.eq(id)).execute() > 0;
    }

    @Override
    public Optional<CounterOfferDto> getCounterOfferData(Long loymaxOfferId) {
        var offersTable = OffersCounterUser.OFFERS_COUNTER_USER;
        return dslContext.select(table.MAX_VALUE, offersTable.VALUE)
                .from(table)
                .leftJoin(offersTable).on(table.ID.eq(offersTable.OFFER_ID))
                .fetchOptionalInto(CounterOfferDto.class);
    }
}
