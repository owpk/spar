package ru.sparural.engine.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.NameCases;
import ru.sparural.engine.entity.CurrencyEntity;
import ru.sparural.engine.repositories.CurrencyRepository;
import ru.sparural.engine.services.CurrencyService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final DtoMapperUtils dtoMapperUtils;
    private final CurrencyRepository currencyRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Currency getByExternalId(String externalId) {
        CurrencyEntity entity = currencyRepository.getByExternalId(externalId).orElse(null);
        if (entity != null) {
            return createDTOFromEntity(entity);
        }
        return null;
    }

    @Override
    public Currency save(Currency dto) {
        CurrencyEntity entity = currencyRepository.saveOrUpdate(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to create account type"));
        var currency = createDTOFromEntity(entity);
        try {
            currency.setNameCases(objectMapper.readValue((String) entity.getNameCases(), NameCases.class));

        } catch (JsonProcessingException e) {
            log.error("Failed convert namecases");
        }
        return currency;
    }


    @Override
    public Currency updateByExternalId(Currency dto) {
        return createDTOFromEntity(currencyRepository.update(createEntityFromDTO(dto))
                .orElseThrow(() -> new ServiceException("Failed to update account type")));
    }

    @Override
    public CurrencyEntity createEntityFromDTO(Currency dto) {
        return dtoMapperUtils.convert(dto, CurrencyEntity.class);
    }

    @Override
    public Currency createDTOFromEntity(CurrencyEntity entity) {
        return dtoMapperUtils.convert(entity, Currency.class);
    }

    @Override
    public Currency get(Long id) {
        var entity = currencyRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        var dto = dtoMapperUtils.convert(Currency.class, () -> entity);
        try {
            dto.setNameCases(objectMapper.readValue((String) entity.getNameCases(), NameCases.class));

        } catch (JsonProcessingException e) {
            log.error("Failed convert namecases");
        }
        return dto;
    }

    @Override
    public List<CurrencyEntity> batchSave(List<CurrencyEntity> currencyEntityList) {
        return currencyRepository.batchSave(currencyEntityList);
    }

    @Override
    public List<CurrencyEntity> fetchAll() {
        return currencyRepository.fetchAll();
    }

    @Override
    public List<CurrencyEntity> fetchAll(Integer offset, Integer limit) {
        return currencyRepository.fetchAll(offset, limit);
    }

    @Override
    public List<CurrencyEntity> fetchByExternalIds(Set<String> extIds) {
        return currencyRepository.fetchByExternalIds(extIds);
    }
}
