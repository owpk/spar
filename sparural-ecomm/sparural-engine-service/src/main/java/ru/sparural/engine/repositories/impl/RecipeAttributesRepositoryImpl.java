package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.repositories.RecipeAttributesRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.RecipeAttributeRecipe;
import ru.sparural.tables.RecipeAttributes;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecipeAttributesRepositoryImpl implements RecipeAttributesRepository {

    private final DSLContext dslContext;
    private final RecipeAttributes table = RecipeAttributes.RECIPE_ATTRIBUTES;
    private final RecipeAttributeRecipe bridgeTable = RecipeAttributeRecipe.RECIPE_ATTRIBUTE_RECIPE;

    @Override
    public Optional<RecipeAttributeEntity> fetchById(Long id) {
        return dslContext.select().from(table).where(table.ID.eq(id))
                .fetchOptionalInto(RecipeAttributeEntity.class);
    }

    @Override
    public List<Long> getAllRecipeAttributeIdByRecipeId(Long recipeId) {
        return dslContext.select(bridgeTable.RECIPE_ATTRIBUTE_ID)
                .from(bridgeTable)
                .where(bridgeTable.RECIPE_ID.eq(recipeId))
                .fetch(bridgeTable.RECIPE_ATTRIBUTE_ID);
    }

    @Override
    public List<RecipeAttributeEntity> list(Integer offset, Integer limit) {
        return dslContext.select().from(table)
                .limit(limit)
                .offset(offset)
                .fetchInto(RecipeAttributeEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table).where(table.ID.eq(id)).execute() > 0;
    }

    @Override
    public Optional<RecipeAttributeEntity> update(Long id, RecipeAttributeEntity entity) {
        var updateStep = dslContext.update(table)
                .set(table.UPDATED_AT, TimeHelper.currentTime());
        if (entity.getDraft() != null)
            updateStep.set(table.DRAFT, entity.getDraft());
        if (entity.getName() != null)
            updateStep.set(table.NAME, entity.getName());
        if (entity.getShowOnPreview() != null)
            updateStep.set(table.SHOW_ON_PREVIEW, entity.getShowOnPreview());
        return updateStep.where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(RecipeAttributeEntity.class);
    }

    @Override
    public Optional<RecipeAttributeEntity> create(RecipeAttributeEntity data) {
        return dslContext.insertInto(table)
                .set(table.DRAFT, data.getDraft())
                .set(table.NAME, data.getName())
                .set(table.SHOW_ON_PREVIEW, data.getShowOnPreview())
                .returning()
                .fetchOptionalInto(RecipeAttributeEntity.class);
    }

}
