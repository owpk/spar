package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;

import java.util.Optional;

public interface AccountsLifeTimesByPeriodRepository {
    Boolean deleteByAccountId(Long accountId);

    Optional<AccountsLifeTimesByPeriod> save(AccountsLifeTimesByPeriod entity);
}
