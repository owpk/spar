package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.engine.repositories.GoodsRepository;
import ru.sparural.engine.repositories.RecipeAttributesRepository;
import ru.sparural.engine.repositories.RecipeRepository;
import ru.sparural.engine.services.RecipeService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.tables.pojos.GoodsItemRecipe;
import ru.sparural.tables.pojos.RecipeAttributeRecipe;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    @Override
    public List<RecipeFullEntity> index(Integer offset, Integer limit) {
        List<RecipeFullEntity> recipeFullEntities = recipeRepository.list(offset, limit);

        recipeFullEntities.forEach(recipe -> {
            recipe.getAttributes().addAll(recipeRepository.getAttributesByRecipeId(recipe.getId()));
            recipe.getGoods().addAll(recipeRepository.getGoodsByRecipeId(recipe.getId()));
        });

        return recipeFullEntities;
    }

    @Override
    public RecipeFullEntity get(Long id) {
        return recipeRepository.fetchById(id);
    }

    @Override
    public RecipeEntity create(List<Long> goodsIds, List<Long> attributesIds, RecipeEntity data) {
        return recipeRepository.create(data, goodsIds, attributesIds);
    }

    @Override
    public RecipeEntity update(Long id, RecipeEntity data, List<Long> goodsIds, List<Long> attributesIds) {
        return recipeRepository.update(id, data, goodsIds, attributesIds);
    }

    @Override
    public Boolean delete(Long id) {
        return recipeRepository.delete(id);
    }
}
