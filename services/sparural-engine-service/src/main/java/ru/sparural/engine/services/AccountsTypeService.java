package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.AccountType;

public interface AccountsTypeService {
    AccountsTypeDto getByCurrencyId(Long currencyId);

    AccountsTypeDto save(AccountsTypeDto dto);

    AccountsTypeDto update(AccountsTypeDto dto);

    AccountType createEntityFromDTO(AccountsTypeDto dto);

    AccountsTypeDto createDTOFromEntity(AccountType entity);
}
