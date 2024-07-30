package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AccountsLifeTimesByTime;

import java.util.List;
import java.util.Optional;

public interface AccountLifeTimesByTimeRepository {

    List<AccountsLifeTimesByTime> fetch(int offset, int limit, long id);

    Boolean deleteByAccountId(Long accountId);

    Optional<AccountsLifeTimesByTime> save(AccountsLifeTimesByTime entity);

    void batchSave(List<AccountsLifeTimesByTime> values);

    void deleteByIds(List<Long> ltbtToDelete);

    List<AccountsLifeTimesByTime> fetchByAccIds(List<Long> accIds);
}
