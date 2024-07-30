package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.*;
import ru.sparural.engine.repositories.AccountsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;

@Service
@RequiredArgsConstructor
public class AccountsRepositoryImpl implements AccountsRepository {
    private static final String LIMITED_TABLE_NAME = "lim_table";

    final private DSLContext dslContext;
    private final Accounts table = Accounts.ACCOUNTS;
    private final Accounts limTable = Accounts.ACCOUNTS.as(LIMITED_TABLE_NAME);
    private final AccountLifeTimesByTime accountLifeTimesByTimeTable = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;

    private final AccountLifeTimesByPeriod accountLifeTimesByPeriodTable = AccountLifeTimesByPeriod.ACCOUNT_LIFE_TIMES_BY_PERIOD;

    @Override
    public Optional<Account> findByIdAndUserId(Long id, Long userId) {
        return dslContext.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.ID.eq(id).and(Accounts.ACCOUNTS.USER_ID.eq(userId)))
                .fetchOptionalInto(Account.class);
    }

    @Override
    public List<Account> getListByUserId(Long userId) {
        return dslContext
                .selectFrom(table)
                .where(table.USER_ID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Account.class);
    }

    // Returns LIMITED_TABLE_NAME as base table !!!
    private SelectOnConditionStep<?> basicSelect(Integer limit, Integer offset) {
        var limitedTable = dslContext.select()
                .from(table)
                .limit(limit).offset(offset)
                .asTable(LIMITED_TABLE_NAME);

        return dslContext.select().from(limitedTable)
                .leftJoin(Currencies.CURRENCIES)
                .on(limTable.CURRENCY_ID.eq(Currencies.CURRENCIES.ID))
                .leftJoin(accountLifeTimesByPeriodTable)
                .on(accountLifeTimesByPeriodTable.ACCOUNT_ID.eq(limTable.ID))
                .leftJoin(accountLifeTimesByTimeTable)
                .on(accountLifeTimesByTimeTable.ACCOUNT_ID.eq(limTable.ID));
    }

    @Override
    public List<AccountFull> getListByUserId(Long userId, Integer offset, Integer limit) {
        return basicSelectWhere(limTable.USER_ID.eq(userId), offset, limit);
    }

    @Override
    public List<AccountFull> fetchByUserIdAndExtCurrencyId(Set<String> currIds, Long userId) {
        var selectStep = basicSelect(null, null)
                .where(limTable.USER_ID.eq(userId).and(Currencies.CURRENCIES.EXTERNAL_ID.in(currIds)));
        return fetchFullAccounts(selectStep);
    }

    private List<AccountFull> basicSelectWhere(Condition condition, Integer offset, Integer limit) {
        var selectStep = basicSelect(limit, offset)
                .where(condition);
        return fetchFullAccounts(selectStep);
    }

    private List<AccountFull> fetchFullAccounts(SelectConditionStep<?> selectConditionStep) {
        var resultMap = new HashMap<Long, AccountFull>();
        selectConditionStep.fetch()
                .forEach(record -> computeRecordToAccountEntity(record, resultMap));
        return new ArrayList<>(resultMap.values());
    }

    private void computeRecordToAccountEntity(Record record, Map<Long, AccountFull> accounts) {
        var id = record.get(limTable.ID);
        var acc = accounts.computeIfAbsent(id, k -> record.into(Accounts.ACCOUNTS.fields()).into(AccountFull.class));

        if (record.get(accountLifeTimesByTimeTable.ID) != null)
            acc.getAccountLifeTimeByTime().computeIfAbsent(accountLifeTimesByTimeTable.ID.get(record),
                    r -> record.into(accountLifeTimesByTimeTable.fields()).into(AccountsLifeTimesByTime.class));

        if (record.get(accountLifeTimesByPeriodTable.ID) != null)
            acc.getAccountLifeTimeByPeriod().computeIfAbsent(accountLifeTimesByPeriodTable.ID.get(record),
                    r -> record.into(accountLifeTimesByPeriodTable.fields()).into(AccountsLifeTimesByPeriod.class));
        if (acc.getCurrency() == null)
            if (record.get(Currencies.CURRENCIES.ID) != null)
                acc.setCurrency(record.into(Currencies.CURRENCIES.fields()).into(CurrencyEntity.class));
    }

    @Override
    public Optional<Account> save(Account entity) {
        return dslContext
                .insertInto(table)
                .set(table.USER_ID, entity.getUserId())
                .set(table.NOT_ACTIVATED_AMOUNT, entity.getNotActivatedAmount())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.USER_ID, table.CURRENCY_ID)
                .doUpdate()
                .set(table.NOT_ACTIVATED_AMOUNT, entity.getNotActivatedAmount())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Account.class);
    }

    @Override
    public List<Account> searchByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired) {
        var lifeTimeTable = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;
        var targetBurningTime = TimeHelper.currentTime() + TimeUnit.DAYS.toSeconds(burningTime);
        return dslContext.select().from(Accounts.ACCOUNTS)
                .leftJoin(lifeTimeTable)
                .on(Accounts.ACCOUNTS.ID.eq(lifeTimeTable.ACCOUNT_ID))
                .where(Accounts.ACCOUNTS.CURRENCY_ID.eq(accountTypeId)
                        .and(lifeTimeTable.DATE.lessThan(targetBurningTime))
                        .and(lifeTimeTable.TRIGGER_FIRED.eq(triggerFired)))
                .fetchInto(Account.class);
    }

    @Override
    public void batchUpdateTriggerFired(List<Long> userIds, boolean firedState) {
        var table = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;
        dslContext.update(table)
                .set(table.TRIGGER_FIRED, firedState)
                .from(Accounts.ACCOUNTS.leftJoin(table)
                        .on(Accounts.ACCOUNTS.ID.eq(table.ACCOUNT_ID)))
                .where(Accounts.ACCOUNTS.USER_ID.in(userIds));
    }

    /**
     * table account types replaced by currencies
     */
    @Override
    @Deprecated
    public List<AccountsTypeDto> fetchAccountTypes(Integer offset, Integer limit) {
        return dslContext.selectFrom(AccountsTypes.ACCOUNTS_TYPES)
                .offset(offset)
                .limit(limit)
                .fetchInto(AccountsTypeDto.class);
    }

    @Override
    public List<Account> batchSave(Collection<Account> accounts) {
        var insert =
                dslContext.insertInto(table, table.AMOUNT, table.USER_ID,
                        table.CURRENCY_ID, table.NOT_ACTIVATED_AMOUNT,
                        table.CREATED_AT);

        for (var rec : accounts)
            insert = insert.values(
                    rec.getAmount(),
                    rec.getUserId(),
                    rec.getCurrencyId(),
                    rec.getNotActivatedAmount(),
                    TimeHelper.currentTime()
            );

        var insertStep = insert.onConflict(table.USER_ID, table.CURRENCY_ID)
                .doUpdate()
                .set(table.AMOUNT, DSL.coalesce(table.as("excluded").AMOUNT, table.AMOUNT))
                .set(table.NOT_ACTIVATED_AMOUNT, DSL.coalesce(table.as("excluded").NOT_ACTIVATED_AMOUNT, table.NOT_ACTIVATED_AMOUNT))
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .returningResult(Accounts.ACCOUNTS.fields());

        return insertStep
                .fetch()
                .into(Account.class);
    }

    @Override
    public void batchUpdateAccountsPojos(ArrayList<Account> accountsToUpdate) {
        var batchQueries = accountsToUpdate.stream()
                .map(x -> dslContext.update(table)
                        .set(table.AMOUNT, x.getAmount())
                        .set(table.NOT_ACTIVATED_AMOUNT, x.getNotActivatedAmount())
                        .set(table.CURRENCY_ID, x.getCurrencyId())
                        .set(table.USER_ID, x.getUserId())
                        .where(table.ID.eq(x.getId()))
                )
                .collect(Collectors.toList());
        dslContext.batch(batchQueries).execute();
    }

}
