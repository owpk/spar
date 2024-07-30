package ru.sparural.engine.services;

import ru.sparural.engine.entity.RecipeAttributeEntity;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecipeAttributesService {

    List<RecipeAttributeEntity> index(Integer offset, Integer limit);

    RecipeAttributeEntity get(Long id);

    RecipeAttributeEntity update(Long id, RecipeAttributeEntity data);

    Boolean delete(Long id);

    RecipeAttributeEntity create(RecipeAttributeEntity data);
}
