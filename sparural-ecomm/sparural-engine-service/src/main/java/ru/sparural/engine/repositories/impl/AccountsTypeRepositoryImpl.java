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
                .where(table.CURRENCY_ID.eq(currencyId))
                .fetchOptionalInto(AccountType.class);
    }

    @Override
    public Optional<AccountType> save(AccountType entity) {
        return dslContext
                .insertInto(table)
                .set(table.NAME, entity.getName())
                .set(table.CURRENCY_ID, entity.getCurrenciesId())
                .set(table.NAME, entity.getName())
                .set(table.ORDER, entity.getOrder())
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .onConflict(table.ID, table.CURRENCY_ID)
                .doUpdate()
                .set(table.NAME, entity.getName())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .set(table.CURRENCY_ID, entity.getCurrenciesId())
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
                .set(table.CURRENCY_ID, entity.getCurrenciesId())
                .set(table.NAME, entity.getName())
                .set(table.ORDER, entity.getOrder())
                .set(table.UPDATED_AT, TimeHelper.currentTime())
                .where(table.ID.eq(entity.getId()))
                .returning()
                .fetchOptionalInto(AccountType.class);

    }


}
