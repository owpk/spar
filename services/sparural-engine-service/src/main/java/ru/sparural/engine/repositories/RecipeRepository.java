package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.tables.pojos.RecipeAttributeRecipe;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecipeRepository {

    Optional<RecipeFullEntity> fetchById(Long id);

    List<RecipeFullEntity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<RecipeEntity> update(Long id, RecipeEntity entity);

    Optional<RecipeEntity> create(RecipeEntity data);

    void bindRecipeToRecipeAttributes(List<RecipeAttributeRecipe> entities);
}
