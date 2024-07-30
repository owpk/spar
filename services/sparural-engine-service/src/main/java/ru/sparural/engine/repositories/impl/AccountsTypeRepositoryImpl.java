package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.AccountType;
import ru.sparural.engine.repositories.AccountsTypeRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.AccountsTypes;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountsTypeRepositoryImpl implements AccountsTypeRepository {

    private final DSLContext dslContext;
    private final AccountsTypes table = AccountsTypes.ACCOUNTS_TYPES;

    @Override
    public Optional<AccountType> getByCurrencyId(Long currencyId) {
        return dslContext
                .selectFrom(table)
                .where(table.CURRENCYID.eq(currencyId))
                .fetchOptionalInto(AccountType.class);
    }

    @Override
    public Optional<AccountType> save(AccountType entity) {
        return dslContext
                .insertInto(table)
                .set(table.NAME, entity.getName())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.NAME, entity.getName())
                .set(table.ORDER, entity.getOrder())
                .set(table.CREATEDAT, TimeHelper.currentTime())
                .onConflict(table.ID, table.CURRENCYID)
                .doUpdate()
                .set(table.NAME, entity.getName())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.NAME, entity.getName())
                .set(table.ORDER, entity.getOrder())
                .returning()
                .fetchOptionalInto(AccountType.class);
    }

    @Override
    public Optional<AccountType> update(AccountType entity) {
        return dslContext
                .update(table)
                .set(table.NAME, entity.getName())
                .set(table.CURRENCYID, entity.getCurrenciesId())
                .set(table.NAME, entity.getName())
                .set(table.ORDER, entity.getOrder())
                .set(table.UPDATEDAT, TimeHelper.currentTime())
                .where(table.ID.eq(entity.getId()))
                .returning()
                .fetchOptionalInto(AccountType.class);

    }


}
