package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.entity.AccountFull;

import java.util.*;

public interface AccountsRepository {

    Optional<Account> findByIdAndUserId(Long id, Long userId);

    List<Account> getListByUserId(Long userId);

    List<AccountFull> getListByUserId(Long userId, Integer offset, Integer limit);

    List<AccountFull> fetchByUserIdAndExtCurrencyId(Set<String> currIds, Long userId);

    Optional<Account> save(Account entity);

    List<Account> searchByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired);

    void batchUpdateTriggerFired(List<Long> userIds, boolean firedState);

    List<AccountsTypeDto> fetchAccountTypes(Integer offset, Integer limit);

    List<Account> batchSave(Collection<Account> accounts);


    void batchUpdateAccountsPojos(ArrayList<Account> accountsToUpdate);
}
