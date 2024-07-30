package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AccountType;

import java.util.Optional;

public interface AccountsTypeRepository {
    Optional<AccountType> getByCurrencyId(Long CurrencyId);

    Optional<AccountType> save(AccountType entity);

    Optional<AccountType> update(AccountType entity);
}
