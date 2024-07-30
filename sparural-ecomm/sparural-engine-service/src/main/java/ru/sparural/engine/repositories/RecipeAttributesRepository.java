package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.RecipeAttributeEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecipeAttributesRepository {

    Optional<RecipeAttributeEntity> fetchById(Long id);

    List<Long> getAllRecipeAttributeIdByRecipeId(Long recipeId);

    List<RecipeAttributeEntity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<RecipeAttributeEntity> update(Long id, RecipeAttributeEntity entity);

    Optional<RecipeAttributeEntity> create(RecipeAttributeEntity data);
}
