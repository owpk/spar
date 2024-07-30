package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.MerchantAttributeCreateOrUpdateDto;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.engine.entity.AttributeEntity;

import java.util.List;

public interface AttributeService {
    List<Attribute> list(Integer offset, Integer limit);

    Attribute get(Long id);

    Attribute create(MerchantAttributeCreateOrUpdateDto attribute);

    Attribute update(Long id, MerchantAttributeCreateOrUpdateDto attribute);

    Boolean delete(Long id);

    Attribute createDto(AttributeEntity entity);

    List<Attribute> createDtoList(List<AttributeEntity> entities);

    AttributeEntity createEntity(MerchantAttributeCreateOrUpdateDto dto);
}
