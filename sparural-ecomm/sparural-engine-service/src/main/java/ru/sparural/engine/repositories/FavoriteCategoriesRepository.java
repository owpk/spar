package ru.sparural.engine.repositories;

import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.entity.FavoriteCategory;
import ru.sparural.engine.entity.FavoriteCategoryUserEntity;

import java.util.List;
import java.util.Optional;

public interface FavoriteCategoriesRepository {
    Optional<FavoriteCategory> get(Long id);

    Optional<FavoriteCategory> get(Long userId, Long id);

    Optional<FavoriteCategory> update(Long id, FavoriteCategoriesDataRequestDto dto);

    List<FavoriteCategory> list(Integer offset, Integer limit);

    List<FavoriteCategory> list(Long userId, Integer offset, Integer limit);

    Optional<Long> findByGoodsGroupUID(String goodsGroupUID);

    void createAsync(FavoriteCategory entity);

    Optional<Long> getByMonthAndYear(Long userId, Integer currentYear, Integer currentMonth);

    Long createMonthAndYear(Long userId, Integer currentYear, Integer currentMonth);

    Optional<FavoriteCategory> createCategoryUser(FavoriteCategoryUserEntity favoriteCategoryUser);

    Long findCategoriesByUserIdAndMonthId(Long userId, Long categoryMonthId);

    Boolean delete(Long id);

    void deleteAsyncByUserId(Long userId);

    void deleteByUserId(Long userId);

    Optional<String> findByIdWithGroupId(Long id);

    void batchCreateCategoryAsync(List<FavoriteCategory> categoriesListEntity);

    void batchCreateCategoryUser(List<FavoriteCategoryUserEntity> collect);
}
