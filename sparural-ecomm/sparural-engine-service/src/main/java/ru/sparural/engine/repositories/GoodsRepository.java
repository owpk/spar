package ru.sparural.engine.repositories;

import org.springframework.lang.Nullable;
import ru.sparural.engine.entity.GoodsEntity;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    List<GoodsEntity> fetch(int offset, int limit, @Nullable String search);

    Optional<GoodsEntity> get(Long id);

    Optional<GoodsEntity> get(String goodsId);

    Optional<GoodsEntity> findByGoodsId(Long id, String goodsId);

    Optional<GoodsEntity> create(GoodsEntity goodsEntity);

    Optional<GoodsEntity> update(Long id, GoodsEntity goodsEntity);

    Optional<GoodsEntity> update(String goodsId, GoodsEntity goodsEntity);

    Boolean delete(Long id);

    Boolean delete(String id);

    List<GoodsEntity> fetchAllByLoymaxIds(List<String> extIds);

    List<Long> getAllRecipeGoodIdByRecipeId(Long recipeId);

    void deleteAllFromBridge(Long recipeId, List<Long> forDelete);

    void insertIntoBridge(Long recipeId, List<Long> forInsert);
}
