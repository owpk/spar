package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.engine.repositories.GoodsRepository;
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
    private final GoodsRepository goodsRepository;

    @Override
    public List<RecipeFullEntity> index(Integer offset, Integer limit) {
        return recipeRepository.list(offset, limit);
    }

    @Override
    public RecipeFullEntity get(Long id) {
        return recipeRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));
    }

    @Override
    public RecipeEntity create(List<Long> goodsIds, List<Long> attributesIds, RecipeEntity data) {
        var entity = recipeRepository.create(data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create recipe: " + data));
        var listRecipesAttributes = attributesIds
                .stream()
                .map(recipeAttributeId -> {
                    var bindEntity = new RecipeAttributeRecipe();
                    bindEntity.setRecipeId(entity.getId());
                    bindEntity.setRecipeAttributeId(recipeAttributeId);
                    return bindEntity;
                }).collect(Collectors.toList());
        var listGoodsRecipes = goodsIds
                .stream()
                .map(goodId -> {
                    var bindEntity = new GoodsItemRecipe();
                    bindEntity.setRecipeId(entity.getId());
                    bindEntity.setGoodsItemId(goodId);
                    return bindEntity;
                }).collect(Collectors.toList());
        recipeRepository.bindRecipeToRecipeAttributes(listRecipesAttributes);
        goodsRepository.bindGoodsToRecipe(listGoodsRecipes);
        return entity;
    }

    @Override
    public RecipeEntity update(Long id, RecipeEntity data) {
        return recipeRepository.update(id, data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update recipe: " + data));
    }

    @Override
    public Boolean delete(Long id) {
        return recipeRepository.delete(id);
    }

}
