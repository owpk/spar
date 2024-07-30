package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.Account;
import ru.sparural.engine.repositories.AccountsRepository;
import ru.sparural.engine.services.AccountsService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsServiceImpl implements AccountsService {

    private final DtoMapperUtils dtoMapperUtils;
    private final AccountsRepository accountsRepository;

    @Override
    public List<AccountsDto> getByUserId(Long userId) {
        List<Account> list = accountsRepository.getListByUserId(userId);
        if (!list.isEmpty()) {
            return createDTOListFromEntity(list);
        }
        return null;
    }

    @Override
    public AccountsDto save(AccountsDto dto) {
        return createDTOFromEntity(accountsRepository.save(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create account")));
    }

    @Override
    public Account createEntityFromDTO(AccountsDto dto) {
        return dtoMapperUtils.convert(dto, Account.class);
    }

    @Override
    public AccountsDto createDTOFromEntity(Account entity) {
        return dtoMapperUtils.convert(entity, AccountsDto.class);
    }

    @Override
    public List<AccountsDto> createDTOListFromEntity(List<Account> list) {
        return dtoMapperUtils.convertList(AccountsDto.class, list);
    }

    @Override
    public List<Account> searchAccountsByFilter(Long accountTypeId, Integer burningTime, boolean triggerFired) {
        return accountsRepository.searchByFilter(accountTypeId, burningTime, triggerFired);
    }

    @Override
    public void setTriggerFired(List<Long> userIds, boolean triggerFiredState) {
        accountsRepository.batchUpdateTriggerFired(userIds, triggerFiredState);
    }

    @Override
    public List<AccountsTypeDto> accountTypesList(Integer offset, Integer limit) {
        return accountsRepository.fetchAccountTypes(offset, limit);
    }
}