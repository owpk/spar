package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PersonalGoodsEntity;

import java.util.List;
import java.util.Optional;

public interface PersonalGoodsRepository {
    Optional<PersonalGoodsEntity> getByUserIdGoodId(Long userId, Long goodsId);

    Optional<PersonalGoodsEntity> saveOrUpdate(PersonalGoodsEntity entity);

    List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> collect);
}
