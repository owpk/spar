package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;
import ru.sparural.engine.repositories.AccountsLifeTimesByPeriodRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.LifeTimesPeriods;
import ru.sparural.tables.AccountLifeTimesByPeriod;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountsLifeTimesByPeriodRepositoryImpl implements AccountsLifeTimesByPeriodRepository {

    final private DSLContext dslContext;
    private final AccountLifeTimesByPeriod table = AccountLifeTimesByPeriod.ACCOUNT_LIFE_TIMES_BY_PERIOD;


    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return dslContext
                .delete(table)
                .where(table.ACCOUNTID.eq(accountId))
                .execute() == 1;
    }

    @Override
    public Optional<AccountsLifeTimesByPeriod> save(AccountsLifeTimesByPeriod entity) {
        return dslContext
                .insertInto(table)
                .set(table.ACCOUNTID, entity.getAccountId())
                .set(table.ACTIVATIONAMOUNT, entity.getActivationAmount())
                .set(table.EXPIRATIONAMOUNT, entity.getExpirationAmount())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .set(table.PERIOD, entity.getPeriod() != null ? LifeTimesPeriods.valueOf(entity.getPeriod()) : LifeTimesPeriods.FromYear)
                .returning()
                .fetchOptionalInto(AccountsLifeTimesByPeriod.class);
    }
}
