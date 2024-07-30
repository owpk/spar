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

@Repository
@RequiredArgsConstructor
public class AccountLifeTimesByTimeRepositoryImpl implements AccountLifeTimesByTimeRepository {

    private final DSLContext dslContext;
    private final AccountLifeTimesByTime table = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;

    @Override
    public List<AccountsLifeTimesByTime> fetch(int offset, int limit, long id) {
        return dslContext
                .selectFrom(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME)
                .where(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME.ACCOUNTID.eq(id))
                .orderBy(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(AccountsLifeTimesByTime.class);
    }

    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return dslContext
                .deleteFrom(table)
                .where(table.ACCOUNTID.eq(accountId))
                .execute() == 1;
    }

    @Override
    public Optional<AccountsLifeTimesByTime> save(AccountsLifeTimesByTime entity) {
        return dslContext
                .insertInto(AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME)
                .set(table.ACCOUNTID, entity.getAccountId())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.DATE, entity.getDate())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(AccountsLifeTimesByTime.class);
    }
}
