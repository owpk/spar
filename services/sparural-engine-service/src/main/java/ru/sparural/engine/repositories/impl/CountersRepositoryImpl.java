package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.repositories.CountersRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Counters;
import ru.sparural.tables.daos.CountersDao;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountersRepositoryImpl implements CountersRepository {
    private final DSLContext dslContext;
    private final Counters table = Counters.COUNTERS;
    private CountersDao dao;

    @PostConstruct
    private void init() {
        dao = new CountersDao(dslContext.configuration());
    }

    @Override
    public Optional<ru.sparural.tables.pojos.Counters> saveOrUpdate(ru.sparural.tables.pojos.Counters counters) {
        return dslContext.insertInto(table)
                .set(table.NAME, counters.getName())
                .set(table.LOYMAX_ID, counters.getLoymaxId())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.LOYMAX_ID)
                .doUpdate()
                .set(table.NAME, counters.getName())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(ru.sparural.tables.pojos.Counters.class);
    }

    @Override
    public List<ru.sparural.tables.pojos.Counters> list(Integer offset, Integer limit) {
        return dslContext.select().from(table)
                .offset(offset)
                .limit(limit)
                .fetchInto(ru.sparural.tables.pojos.Counters.class);
    }

    @Override
    public Optional<ru.sparural.tables.pojos.Counters> fetchById(Long id) {
        return dslContext.select().from(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(ru.sparural.tables.pojos.Counters.class);
    }

    @Override
    public Optional<ru.sparural.tables.pojos.Counters> update(Long id, ru.sparural.tables.pojos.Counters data) {
        return dslContext.update(table)
                .set(table.NAME, data.getName())
                .set(table.LOYMAX_ID, data.getLoymaxId())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(ru.sparural.tables.pojos.Counters.class);
    }

    @Override
    public Boolean delete(Long id) {
        dao.deleteById(id);
        return true;
    }
}
