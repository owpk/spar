package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.repositories.RecipeAttributesRepository;
import ru.sparural.engine.services.RecipeAttributesService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecipeAttributesServiceImpl implements RecipeAttributesService {

    private final RecipeAttributesRepository recipeAttributesRepository;

    @Override
    public List<RecipeAttributeEntity> index(Integer offset, Integer limit) {
        return recipeAttributesRepository.list(offset, limit);
    }

    @Override
    public RecipeAttributeEntity get(Long id) {
        return recipeAttributesRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe attribute not found with id: " + id));
    }

    @Override
    public RecipeAttributeEntity update(Long id, RecipeAttributeEntity data) {
        return recipeAttributesRepository.update(id, data)
                .orElseThrow(() -> new RuntimeException("Cannot update recipe attribute with id: " + id));
    }

    @Override
    public Boolean delete(Long id) {
        return recipeAttributesRepository.delete(id);
    }

    @Override
    public RecipeAttributeEntity create(RecipeAttributeEntity data) {
        return recipeAttributesRepository.create(data)
                .orElseThrow(() -> new RuntimeException("Cannot create recipe attribute: " + data));
    }
}
