package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.entity.CurrencyEntity;

import java.util.List;
import java.util.Set;


public interface CurrencyService {
    Currency getByExternalId(String externalId);

    Currency save(Currency dto);

    Currency updateByExternalId(Currency dto);

    CurrencyEntity createEntityFromDTO(Currency dto);

    Currency createDTOFromEntity(CurrencyEntity entity);

    Currency get(Long id);

    List<CurrencyEntity> batchSave(List<CurrencyEntity> currencyEntityList);

    List<CurrencyEntity> fetchAll();

    List<CurrencyEntity> fetchAll(Integer offset, Integer limit);

    List<CurrencyEntity> fetchByExternalIds(Set<String> collect);
}
