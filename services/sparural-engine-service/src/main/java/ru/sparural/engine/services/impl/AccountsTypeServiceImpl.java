package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.account.AccountsTypeDto;
import ru.sparural.engine.entity.AccountType;
import ru.sparural.engine.repositories.AccountsTypeRepository;
import ru.sparural.engine.services.AccountsTypeService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class AccountsTypeServiceImpl implements AccountsTypeService {

    private final DtoMapperUtils dtoMapperUtils;
    private final AccountsTypeRepository accountsTypeRepository;

    @Override
    public AccountsTypeDto getByCurrencyId(Long currencyId) {
        AccountType entity = accountsTypeRepository
                .getByCurrencyId(currencyId)
                .orElse(null);
        if (entity != null) {
            return createDTOFromEntity(entity);
        }
        return null;
    }

    @Override
    public AccountsTypeDto save(AccountsTypeDto dto) {
        return createDTOFromEntity(accountsTypeRepository.save(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create account type")));
    }

    @Override
    public AccountsTypeDto update(AccountsTypeDto dto) {
        return createDTOFromEntity(accountsTypeRepository.update(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to update account type")));
    }

    @Override
    public AccountType createEntityFromDTO(AccountsTypeDto dto) {
        return dtoMapperUtils.convert(dto, AccountType.class);
    }

    @Override
    public AccountsTypeDto createDTOFromEntity(AccountType entity) {
        return dtoMapperUtils.convert(entity, AccountsTypeDto.class);
    }
}
