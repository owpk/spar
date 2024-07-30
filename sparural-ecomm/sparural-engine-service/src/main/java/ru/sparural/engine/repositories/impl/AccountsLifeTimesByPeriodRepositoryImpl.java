package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;
import ru.sparural.engine.entity.enums.LifeTimesPeriods;
import ru.sparural.engine.repositories.AccountsLifeTimesByPeriodRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.AccountLifeTimesByPeriod;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountsLifeTimesByPeriodRepositoryImpl implements AccountsLifeTimesByPeriodRepository {

    final private DSLContext dslContext;
    private final AccountLifeTimesByPeriod table = AccountLifeTimesByPeriod.ACCOUNT_LIFE_TIMES_BY_PERIOD;


    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return dslContext
                .delete(table)
                .where(table.ACCOUNT_ID.eq(accountId))
                .execute() == 1;
    }

    @Override
    public Optional<AccountsLifeTimesByPeriod> save(AccountsLifeTimesByPeriod entity) {
        return dslContext
                .insertInto(table)
                .set(table.ACCOUNT_ID, entity.getAccountId())
                .set(table.ACTIVATION_AMOUNT, entity.getActivationAmount())
                .set(table.EXPIRATION_AMOUNT, entity.getExpirationAmount())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .set(table.PERIOD, entity.getPeriod() != null ? LifeTimesPeriods.valueOf(entity.getPeriod()).getVal() :
                        LifeTimesPeriods.FromYear.getVal())
                .returning()
                .fetchOptionalInto(AccountsLifeTimesByPeriod.class);
    }

    @Override
    public List<AccountsLifeTimesByPeriod> fetchByAccountsIds(List<Long> accIds) {
        return dslContext.selectFrom(table).where(table.ACCOUNT_ID.in(accIds)).fetchInto(AccountsLifeTimesByPeriod.class);
    }

    @Override
    public void deleteByIds(List<Long> idsToDelete) {
        dslContext.deleteFrom(table).where(table.ID.in(idsToDelete)).execute();
    }

    @Override
    public void batchSave(List<AccountsLifeTimesByPeriod> values) {
        var queries = values.stream()
                .map(e -> dslContext.insertInto(table)
                        .set(table.ACCOUNT_ID, e.getAccountId())
                        .set(table.PERIOD, LifeTimesPeriods.valueOf(e.getPeriod()).getVal())
                        .set(table.EXPIRATION_AMOUNT, e.getExpirationAmount())
                        .set(table.ACTIVATION_AMOUNT, e.getActivationAmount())
                        .set(table.CREATED_AT, TimeHelper.currentTime())
                ).collect(Collectors.toList());
        dslContext.batch(queries).execute();
    }
}
