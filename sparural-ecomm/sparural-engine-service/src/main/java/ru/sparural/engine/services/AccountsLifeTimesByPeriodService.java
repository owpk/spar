package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;

import java.util.Collection;
import java.util.List;

public interface AccountsLifeTimesByPeriodService {
    Boolean deleteByAccountId(Long accountId);

    AccountsLifeTimesByPeriodDto save(AccountsLifeTimesByPeriodDto dto);

    AccountsLifeTimesByPeriod createEntityFromDTO(AccountsLifeTimesByPeriodDto dto);

    AccountsLifeTimesByPeriodDto createDTOFromEntity(AccountsLifeTimesByPeriod entity);

    List<AccountsLifeTimesByPeriod> fetchByAccId(List<Long> collect);

    void deleteByAccountIds(List<Long> ltbpToDelete);

    void batchSave(List<AccountsLifeTimesByPeriod> values);
}
