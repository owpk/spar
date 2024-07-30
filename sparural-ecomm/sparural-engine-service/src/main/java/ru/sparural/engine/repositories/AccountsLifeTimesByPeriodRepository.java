package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountsLifeTimesByPeriodRepository {
    Boolean deleteByAccountId(Long accountId);

    Optional<AccountsLifeTimesByPeriod> save(AccountsLifeTimesByPeriod entity);

    List<AccountsLifeTimesByPeriod> fetchByAccountsIds(List<Long> accIds);

    void deleteByIds(List<Long> ltbpToDelete);

    void batchSave(List<AccountsLifeTimesByPeriod> values);
}
