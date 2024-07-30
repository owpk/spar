package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.CurrencyEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CurrencyRepository {
    Optional<CurrencyEntity> getByExternalId(String externalId);

    Optional<CurrencyEntity> saveOrUpdate(CurrencyEntity entity);

    Optional<CurrencyEntity> update(CurrencyEntity entity);

    Optional<CurrencyEntity> get(Long id);

    List<CurrencyEntity> batchSave(List<CurrencyEntity> currencyEntityList);

    List<CurrencyEntity> fetchAll();

    List<CurrencyEntity> fetchAll(Integer offset, Integer limit);

    List<CurrencyEntity> fetchByExternalIds(Set<String> extIds);
}
