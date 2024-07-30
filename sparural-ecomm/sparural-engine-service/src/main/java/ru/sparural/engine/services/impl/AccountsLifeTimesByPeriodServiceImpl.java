package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.entity.AccountsLifeTimesByPeriod;
import ru.sparural.engine.repositories.AccountsLifeTimesByPeriodRepository;
import ru.sparural.engine.services.AccountsLifeTimesByPeriodService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsLifeTimesByPeriodServiceImpl implements AccountsLifeTimesByPeriodService {

    private final DtoMapperUtils dtoMapperUtils;
    private final AccountsLifeTimesByPeriodRepository accountsLifeTimesByPeriodRepository;

    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return accountsLifeTimesByPeriodRepository.deleteByAccountId(accountId);
    }

    @Override
    public AccountsLifeTimesByPeriodDto save(AccountsLifeTimesByPeriodDto dto) {
        return createDTOFromEntity(accountsLifeTimesByPeriodRepository.save(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create accountsLifeTimesByPeriod")));
    }

    @Override
    public AccountsLifeTimesByPeriod createEntityFromDTO(AccountsLifeTimesByPeriodDto dto) {
        return dtoMapperUtils.convert(dto, AccountsLifeTimesByPeriod.class);
    }

    @Override
    public AccountsLifeTimesByPeriodDto createDTOFromEntity(AccountsLifeTimesByPeriod entity) {
        return dtoMapperUtils.convert(entity, AccountsLifeTimesByPeriodDto.class);
    }

    @Override
    public List<AccountsLifeTimesByPeriod> fetchByAccId(List<Long> accIds) {
        return accountsLifeTimesByPeriodRepository.fetchByAccountsIds(accIds);
    }

    @Override
    public void deleteByAccountIds(List<Long> ltbpToDelete) {
        accountsLifeTimesByPeriodRepository.deleteByIds(ltbpToDelete);
    }

    @Override
    public void batchSave(List<AccountsLifeTimesByPeriod> values) {
        accountsLifeTimesByPeriodRepository.batchSave(values);
    }
}
