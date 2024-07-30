package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.entity.AccountsLifeTimesByTime;
import ru.sparural.engine.repositories.AccountLifeTimesByTimeRepository;
import ru.sparural.engine.repositories.AccountsRepository;
import ru.sparural.engine.services.AccountsLifeTimesByTimeService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountsLifeTimesByTimeServiceImpl implements AccountsLifeTimesByTimeService {

    private final AccountLifeTimesByTimeRepository accountLifeTimesByTimeRepository;
    private final AccountsRepository accountsRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<AccountsLifeTimesByTime> list(int offset, int limit, long id, long userId) {
        if (accountsRepository.findByIdAndUserId(id, userId).isPresent()) {
            return accountLifeTimesByTimeRepository.fetch(offset, limit, id);
        } else {
            throw new ResourceNotFoundException("Resource not found");
        }
    }

    @Override
    public Boolean deleteByAccountId(Long accountId) {
        return accountLifeTimesByTimeRepository.deleteByAccountId(accountId);
    }

    @Override
    public AccountsLifeTimesByTimeDTO save(AccountsLifeTimesByTimeDTO dto) {
        return createDTOFromEntity(accountLifeTimesByTimeRepository.save(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create accountsLifeTimesByPeriod")));
    }

    @Override
    public AccountsLifeTimesByTime createEntityFromDTO(AccountsLifeTimesByTimeDTO dto) {
        return dtoMapperUtils.convert(dto, AccountsLifeTimesByTime.class);
    }

    @Override
    public AccountsLifeTimesByTimeDTO createDTOFromEntity(AccountsLifeTimesByTime entity) {
        return dtoMapperUtils.convert(entity, AccountsLifeTimesByTimeDTO.class);
    }

    @Override
    public List<AccountsLifeTimesByTime> fetchByAccId(List<Long> accIds) {
        return accountLifeTimesByTimeRepository.fetchByAccIds(accIds);
    }

    @Override
    public void deleteByIds(List<Long> ltbtToDelete) {
        accountLifeTimesByTimeRepository.deleteByIds(ltbtToDelete);
    }

    @Override
    public void batchSave(List<AccountsLifeTimesByTime> values) {
        accountLifeTimesByTimeRepository.batchSave(values);
    }
}
