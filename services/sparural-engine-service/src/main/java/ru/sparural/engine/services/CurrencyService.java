package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.entity.CurrencyEntity;

import java.util.List;


public interface CurrencyService {
    Currency getByExternalId(String externalId);

    Currency save(Currency dto);

    Currency updateByExternalId(Currency dto);

    CurrencyEntity createEntityFromDTO(Currency dto);

    Currency createDTOFromEntity(CurrencyEntity entity);

    Currency get(Long id);

    List<CurrencyEntity> batchSave(List<CurrencyEntity> currencyEntityList);
}
