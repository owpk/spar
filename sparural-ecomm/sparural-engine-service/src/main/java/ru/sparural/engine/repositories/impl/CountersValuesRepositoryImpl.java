package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.CountersValuesRepository;
import ru.sparural.tables.CountersValues;
import ru.sparural.tables.daos.CountersValuesDao;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class CountersValuesRepositoryImpl implements CountersValuesRepository {
    private final DSLContext dslContext;
    private final CountersValues table = CountersValues.COUNTERS_VALUES;
    private CountersValuesDao dao;

    @PostConstruct
    private void init() {
        dao = new CountersValuesDao(dslContext.configuration());
    }

    @Override
    public boolean bindCounterToUser(Long counterId, Long userId, Integer value) {
        return dslContext.insertInto(table)
                .set(table.COUNTER_ID, counterId)
                .set(table.USER_ID, userId)
                .set(table.VALUE, value)
                .onConflict(table.COUNTER_ID, table.USER_ID)
                .doUpdate()
                .set(table.VALUE, value)
                .execute() > 0;
    }

}
