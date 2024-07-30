package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.tables.pojos.RecipeAttributeRecipe;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecipeRepository {

    RecipeFullEntity fetchById(Long id);

    List<RecipeFullEntity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<RecipeEntity> update(Long id, RecipeEntity entity);

    RecipeEntity update(
            Long recipeId,
            RecipeEntity recipe,
            List<Long> goodsIds,
            List<Long> attributesIds
    );

    Optional<RecipeEntity> create(RecipeEntity data);

    RecipeEntity create(
            RecipeEntity recipe,
            List<Long> goodsIds,
            List<Long> attributesIds
    );

    List<RecipeAttributeEntity> getAttributesByRecipeId(Long recipeId);

    List<GoodsEntity> getGoodsByRecipeId(Long recipeId);

    @Deprecated
    void bindRecipeToRecipeAttributes(List<RecipeAttributeRecipe> entities);
}
