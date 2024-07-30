package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CategoriesDto;
import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.api.dto.MetaDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.entity.FavoriteCategory;
import ru.sparural.engine.entity.FavoriteCategoryUserEntity;
import ru.sparural.engine.loymax.rest.dto.categories.LoymaxCategoryItem;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.services.LoymaxSettingsService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.FavoriteCategoriesRepository;
import ru.sparural.engine.services.FavoriteCategoriesService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteCategoriesServiceImpl implements FavoriteCategoriesService {
    private final FavoriteCategoriesRepository favoriteCategoriesRepository;
    private final DtoMapperUtils dtoMapperUtils;
    private final LoymaxService loymaxService;
    private final LoymaxSettingsService loymaxSettingsService;

    @Override
    public CategoriesDto list(Long userId,
                              Integer offset,
                              Integer limit) {
        return getList(userId, LocalDate.now().getMonth().getValue(), LocalDate.now().getYear(),
                offset, limit);
    }

    @Override
    public CategoriesDto list(Integer offset,
                              Integer limit) {
        return CategoriesDto.builder()
                .data(createListDto(favoriteCategoriesRepository.list(offset, limit)))
                .build();
    }


    @Override
    public CategoryDto get(Long id) {
        return createDto(favoriteCategoriesRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public CategoryDto get(Long userId, Long id) {
        return createDto(favoriteCategoriesRepository.get(userId, id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public CategoryDto update(Long id, FavoriteCategoriesDataRequestDto dto) {
        return createDto(favoriteCategoriesRepository.update(id, dto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public CategoryDto createDto(FavoriteCategory entity) {
        return dtoMapperUtils.convert(CategoryDto.class, () -> entity);
    }

    @Override
    public FavoriteCategory createEntity(CategoryDto dto) {
        return dtoMapperUtils.convert(FavoriteCategory.class, () -> dto);
    }


    @Override
    public List<CategoryDto> createListDto(List<FavoriteCategory> entityList) {
        return dtoMapperUtils.convertList(CategoryDto.class, () -> entityList);
    }

    @Override
    public List<FavoriteCategory> createListEntity(List<CategoryDto> dtoList) {
        return dtoMapperUtils.convertList(FavoriteCategory.class, () -> dtoList);
    }

    @Override
    public Long getIdOrCreateCategoryMonth(Long userId, Integer year, Integer month) {
        Optional<Long> optionalCategoryMonthId = favoriteCategoriesRepository
                .getByMonthAndYear(userId, year, month);
        return optionalCategoryMonthId
                .orElseGet(() -> favoriteCategoriesRepository
                        .createMonthAndYear(userId, year, month));
    }

    @Override
    public Long getIdOrCreateFavoriteCategory(FavoriteCategory categoryEntity) {
        Optional<Long> optionalCategoryId = favoriteCategoriesRepository
                .findByGoodsGroupUID(categoryEntity.getGoodsGroupUID());
        return optionalCategoryId.get();
    }

    @Override
    public void updateMaxCount(Long maxCount) {
        loymaxSettingsService.updateMaxCount(maxCount);
    }

    @Override
    public CategoriesDto getList(Long userId,
                                 Integer month,
                                 Integer year,
                                 Integer offset,
                                 Integer limit) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var categories = loymaxService.getUserFavoriteCategories(loymaxUser);

        if (categories == null)
            return new CategoriesDto();

        List<LoymaxCategoryItem> categoriesList = categories.getCategories();

        List<FavoriteCategory> categoriesListEntity = categoriesList.stream()
                .map(x -> {
                    var category = new FavoriteCategory();
                    category.setId(Long.parseLong(x.getId()));
                    category.setUserId(userId);
                    category.setName(x.getName());
                    category.setAccepted(x.getAccepted());
                    category.setPreferenceType(x.getPreferenceType());
                    category.setGoodsGroupUID(x.getGoodsGroupUID());
                    category.setPreferenceValue(x.getPreferenceValue());
                    category.setStartActiveDate(LoymaxTimeToSparTimeAdapter
                            .convertToEpochSeconds(x.getStartActiveDate()));
                    category.setEndActiveDate(LoymaxTimeToSparTimeAdapter
                            .convertToEpochSeconds(x.getEndActiveDate()));
                    category.setLoymaxId(x.getId());
                    return category;
                }).collect(Collectors.toList());

        favoriteCategoriesRepository.batchCreateCategoryAsync(categoriesListEntity);

        Long categoryMonthId = getIdOrCreateCategoryMonth(userId, year, month);

        Long maxCount = categories.getMaxCount();

        var t = new Thread(() -> updateMaxCount(maxCount));
        t.setDaemon(true);
        t.start();

        favoriteCategoriesRepository.batchCreateCategoryUser(
                categoriesListEntity.stream().map(v -> {
                    var favCatUser = new FavoriteCategoryUserEntity();
                    favCatUser.setName(v.getName());
                    favCatUser.setCategoryId(getIdOrCreateFavoriteCategory(v));
                    favCatUser.setAccepted(v.getAccepted());
                    favCatUser.setUserId(userId);
                    favCatUser.setPreferenceType(v.getPreferenceType());
                    favCatUser.setPreferenceValue(v.getPreferenceValue());
                    favCatUser.setStartActiveDate(v.getStartActiveDate());
                    favCatUser.setEndActiveDate(v.getEndActiveDate());
                    favCatUser.setCategoryMonthId(categoryMonthId);
                    return favCatUser;
                }).collect(Collectors.toList()));

        var accepted = categoriesListEntity.stream()
                .anyMatch(FavoriteCategory::getAccepted);

        var meta = MetaDto.builder()
                .maxCount(maxCount)
                .accepted(accepted)
                .year(year)
                .month(month)
                .build();

        var allList = favoriteCategoriesRepository.list(userId, offset, limit);

        if (meta.getAccepted()) {
            var listWithAccepted = allList.stream()
                    .filter(FavoriteCategory::getAccepted)
                    .collect(Collectors.toList());
            return CategoriesDto.builder()
                    .data(createListDto(listWithAccepted))
                    .meta(meta).build();
        }

        return CategoriesDto.builder()
                .data(createListDto(allList))
                .meta(meta).build();
    }

    @Override
    public Boolean delete(Long id) {
        favoriteCategoriesRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return favoriteCategoriesRepository.delete(id);

    }

    @Override
    public String findByIdWithGroupId(Long id) {
        return favoriteCategoriesRepository
                .findByIdWithGroupId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

}
