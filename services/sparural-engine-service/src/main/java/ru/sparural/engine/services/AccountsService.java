package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;

import java.util.List;

public interface AccountsService {
    List<AccountsDto> getByUserId(Long currencyId);

    AccountsDto save(AccountsDto dto);

    Account createEntityFromDTO(AccountsDto dto);

    AccountsDto createDTOFromEntity(Account entity);

    List<AccountsDto> createDTOListFromEntity(List<Account> list);

    List<Account> searchAccountsByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired);

    void setTriggerFired(List<Long> userIds, boolean triggerFiredState);

    List<AccountsTypeDto> accountTypesList(Integer offset, Integer limit);
}
