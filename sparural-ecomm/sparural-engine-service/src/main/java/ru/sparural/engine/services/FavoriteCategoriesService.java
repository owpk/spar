package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CategoriesDto;
import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.entity.FavoriteCategory;

import java.util.List;

public interface FavoriteCategoriesService {
    CategoriesDto list(Long userId, Integer offset, Integer limit);

    CategoriesDto list(Integer offset, Integer limit);

    CategoryDto get(Long id);

    CategoryDto get(Long userId, Long id);

    CategoryDto update(Long id, FavoriteCategoriesDataRequestDto dto);

    CategoryDto createDto(FavoriteCategory entity);

    FavoriteCategory createEntity(CategoryDto categoryDto);

    List<CategoryDto> createListDto(List<FavoriteCategory> entityList);

    List<FavoriteCategory> createListEntity(List<CategoryDto> list);

    Long getIdOrCreateFavoriteCategory(FavoriteCategory entity);

    void updateMaxCount(Long maxCount);

    Long getIdOrCreateCategoryMonth(Long userId, Integer year, Integer month);

    CategoriesDto getList(Long userId, Integer month, Integer year, Integer offset, Integer limit);

    Boolean delete(Long id);

    String findByIdWithGroupId(Long id);
}
