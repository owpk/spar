package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsForSaveDto;
import ru.sparural.engine.entity.PersonalGoodsEntity;

import java.util.List;

public interface PersonalGoodsService {
    PersonalGoodsDto getByUserIdGoodsId(Long userId, Long goodsId);

    PersonalGoodsDto createDto(PersonalGoodsEntity entity);

    PersonalGoodsEntity createEntity(PersonalGoodsDto dto);

    PersonalGoodsDto saveOrUpdate(PersonalGoodsForSaveDto dto);

    List<PersonalGoodsEntity> batchSave(List<PersonalGoodsForSaveDto> dto);

    List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> collect);
}
