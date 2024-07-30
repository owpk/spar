package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountsRepository {

    Optional<Account> findByIdAndUserId(Long id, Long userId);

    List<Account> getListByUserId(Long userId);

    Optional<Account> save(Account entity);

    List<Account> searchByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired);

    void batchUpdateTriggerFired(List<Long> userIds, boolean firedState);

    List<AccountsTypeDto> fetchAccountTypes(Integer offset, Integer limit);
}
