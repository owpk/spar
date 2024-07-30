package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.AttributeEntity;
import ru.sparural.engine.entity.MerchantAttribute;

import java.util.List;
import java.util.Optional;

public interface AttributeRepository {

    Optional<AttributeEntity> get(Long id);

    List<AttributeEntity> list(Integer offset, Integer limit);

    Optional<AttributeEntity> create(AttributeEntity entity);

    Optional<AttributeEntity> update(Long id, AttributeEntity entity);

    Boolean delete(Long id);

    void deleteAllForMerchant(Long merchantId);

    Boolean findDraftById(Long id);

    List<MerchantAttribute> listOfMerchants(Long id);

    List<Long> listIdOfMerchants(Long id);

    void saveMerchantAttributesOfMerchant(Long attributeId, Long merchantId);

}
