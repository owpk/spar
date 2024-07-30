package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.entity.AccountFull;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.loymax.rest.dto.account.LoymaxUserBalanceInfoDto;

import java.util.ArrayList;
import java.util.List;

public interface AccountsService {
    List<AccountsDto> getByUserId(Long currencyId);

    AccountsDto save(AccountsDto dto);

    Account createEntityFromDTO(AccountsDto dto);

    AccountsDto createDTOFromEntity(Account entity);
    AccountsDto createDTOFromEntity(AccountFull entity);

    List<AccountsDto> createDTOListFromEntity(List<Account> list);

    List<Account> searchAccountsByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired);

    void setTriggerFired(List<Long> userIds, boolean triggerFiredState);

    List<AccountsTypeDto> accountTypesList(Integer offset, Integer limit);

    void batchUpdateAccountsPojos(ArrayList<Account> accountsToUpdate);

    List<AccountFull> list(Long userId, Integer offset, Integer limit);

    List<AccountFull> selectFromLoymaxAndSave(Long userId, List<LoymaxUserBalanceInfoDto> loymaxAccounts);
}
