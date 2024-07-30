package ru.sparural.engine.services;

import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecipeService {
    List<RecipeFullEntity> index(Integer offset, Integer limit);

    RecipeFullEntity get(Long id);

    RecipeEntity create(List<Long> goodsIds, List<Long> attributesIds, RecipeEntity data);

    RecipeEntity update(Long id, RecipeEntity map);

    Boolean delete(Long id);
}
