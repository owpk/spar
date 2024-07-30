package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.OffersCounterUserRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.pojos.OffersCounterUser;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class OffersCounterUserRepositoryImpl implements OffersCounterUserRepository {
    private final DSLContext dslContext;
    private final ru.sparural.tables.OffersCounterUser table = ru.sparural.tables.OffersCounterUser.OFFERS_COUNTER_USER;

    @Override
    public void saveOrUpdate(OffersCounterUser data) {
        dslContext.insertInto(table)
                .set(table.OFFER_ID, data.getOfferId())
                .set(table.USER_ID, data.getUserId())
                .set(table.VALUE, data.getValue())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.OFFER_ID, table.USER_ID)
                .doUpdate()
                .set(table.VALUE, data.getValue())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .executeAsync();
    }

}