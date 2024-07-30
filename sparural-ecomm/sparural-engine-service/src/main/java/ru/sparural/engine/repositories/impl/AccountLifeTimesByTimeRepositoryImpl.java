package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.sparural.engine.entity.AccountsLifeTimesByTime;
import ru.sparural.engine.repositories.AccountLifeTimesByTimeRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.AccountLifeTimesByTime;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountLifeTimesByTimeRepositoryImpl implements AccountLifeTimesByTimeRepository {

    private final DSLContext dslContext;
    private final AccountLifeTimesByTime table = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;

    @Override
    public List<AccountsLifeTimesByTime> fetch(int offset, int limit, long id) {
        return dslContext
                .selectFrom(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME)
                .where(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME.ACCOUNT_ID.eq(id))
                .orderBy(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(AccountsLifeTimesByTime.class);
    }

    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return dslContext
                .deleteFrom(table)
                .where(table.ACCOUNT_ID.eq(accountId))
                .execute() == 1;
    }

    @Override
    public Optional<AccountsLifeTimesByTime> save(AccountsLifeTimesByTime entity) {
        return dslContext
                .insertInto(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME)
                .set(table.ACCOUNT_ID, entity.getAccountId())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.DATE, entity.getDate())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(AccountsLifeTimesByTime.class);
    }

    @Override
    public void batchSave(List<AccountsLifeTimesByTime> values) {
        var queries = values.stream()
                .map(e -> dslContext.insertInto(table)
                        .set(table.ACCOUNT_ID, e.getAccountId())
                        .set(table.DATE, e.getDate())
                        .set(table.AMOUNT, e.getAmount())
                        .set(table.CREATED_AT, TimeHelper.currentTime())
                ).collect(Collectors.toList());
        dslContext.batch(queries).execute();
    }

    @Override
    public void deleteByIds(List<Long> ltbtToDelete) {
        dslContext.deleteFrom(table).where(table.ID.in(ltbtToDelete)).execute();
    }

    @Override
    public List<AccountsLifeTimesByTime> fetchByAccIds(List<Long> accIds) {
        return dslContext.selectFrom(table).where(table.ACCOUNT_ID.in(accIds))
                .fetchInto(AccountsLifeTimesByTime.class);
    }
}
