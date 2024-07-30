package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;

public interface AccountsLifeTimesByPeriodService {
    Boolean deleteByAccountId(Long accountId);

    AccountsLifeTimesByPeriodDto save(AccountsLifeTimesByPeriodDto dto);

    AccountsLifeTimesByPeriod createEntityFromDTO(AccountsLifeTimesByPeriodDto dto);

    AccountsLifeTimesByPeriodDto createDTOFromEntity(AccountsLifeTimesByPeriod entity);
}
