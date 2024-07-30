package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.loymax.enums.PersonalGoodsName;

import java.util.List;

public interface PersonalGoodsService {
    PersonalGoodsEntity getByUserIdGoodsId(Long userId, Long goodsId);

    PersonalGoodsDto createDto(PersonalGoodsEntity entity);

    PersonalGoodsEntity createEntity(PersonalGoodsDto dto);

    List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> entities);

    List<PersonalGoodsEntity> fetchByUserId(Integer offset, Integer limit, Long userId);

    List<PersonalGoodsEntity> fetchByUserId(Integer offset, Integer limit, Long userId, PersonalGoodsName personalGoodsName);

    PersonalGoodsEntity saveOrUpdate(PersonalGoodsEntity goodEntity);
}
