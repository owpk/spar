package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.repositories.AccountsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.AccountLifeTimesByTime;
import ru.sparural.tables.Accounts;
import ru.sparural.tables.AccountsTypes;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AccountsRepositoryImpl implements AccountsRepository {

    final private DSLContext dslContext;
    private final Accounts table = Accounts.ACCOUNTS;

    @Override
    public Optional<Account> findByIdAndUserId(Long id, Long userId) {
        return dslContext.selectFrom(Accounts.ACCOUNTS)
                .where(Accounts.ACCOUNTS.ID.eq(id).and(Accounts.ACCOUNTS.USERID.eq(userId)))
                .fetchOptionalInto(Account.class);
    }

    @Override
    public List<Account> getListByUserId(Long userId) {
        return dslContext
                .selectFrom(table)
                .where(table.USERID.eq(userId))
                .orderBy(table.ID.desc())
                .fetch()
                .into(Account.class);
    }

    @Override
    public Optional<Account> save(Account entity) {
        return dslContext
                .insertInto(table)
                .set(table.ACCOUNTTYPEID, entity.getAccountTypeIdField())
                .set(table.USERID, entity.getUserId())
                .set(table.NOTACTIVATEDAMOUNT, entity.getNotActivatedAmount())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.USERID, table.ACCOUNTTYPEID)
                .doUpdate()
                .set(table.NOTACTIVATEDAMOUNT, entity.getNotActivatedAmount())
                .set(table.AMOUNT, entity.getAmount())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(Account.class);
    }

    @Override
    public List<Account> searchByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired) {
        var lifeTimeTable = AccountLifeTimesByTime.ACCOUNT_LIFE_TIMES_BY_TIME;
        var targetBurningTime = TimeHelper.currentTime() + TimeUnit.DAYS.toSeconds(burningTime);
        return dslContext.select().from(Accounts.ACCOUNTS)
                .leftJoin(lifeTimeTable)
                .on(Accounts.ACCOUNTS.ID.eq(lifeTimeTable.ACCOUNTID))
                .where(Accounts.ACCOUNTS.ACCOUNTTYPEID.eq(accountTypeId)
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
                        .on(Accounts.ACCOUNTS.ID.eq(table.ACCOUNTID)))
                .where(Accounts.ACCOUNTS.USERID.in(userIds));
    }

    @Override
    public List<AccountsTypeDto> fetchAccountTypes(Integer offset, Integer limit) {
        return dslContext.selectFrom(AccountsTypes.ACCOUNTS_TYPES)
                .offset(offset)
                .limit(limit)
                .fetchInto(AccountsTypeDto.class);
    }
}
