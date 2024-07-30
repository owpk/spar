package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.engine.repositories.RecipeRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Goods;
import ru.sparural.tables.GoodsItemRecipe;
import ru.sparural.tables.RecipeAttributes;
import ru.sparural.tables.Recipes;
import ru.sparural.tables.daos.RecipeAttributeRecipeDao;
import ru.sparural.tables.pojos.RecipeAttributeRecipe;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeRepository {

    private final DSLContext dslContext;
    private final Recipes recipesTable = Recipes.RECIPES;
    private final RecipeAttributes recipeAttributesTable =
            RecipeAttributes.RECIPE_ATTRIBUTES;
    private final ru.sparural.tables.RecipeAttributeRecipe recAttrRecTable =
            ru.sparural.tables.RecipeAttributeRecipe.RECIPE_ATTRIBUTE_RECIPE;
    private final GoodsItemRecipe goodsItemRecipeTable =
            GoodsItemRecipe.GOODS_ITEM_RECIPE;
    private final Goods goodsTable = Goods.GOODS;

    private RecipeAttributeRecipeDao recipeAttributeRecipeDao;

    @PostConstruct
    private void init() {
        recipeAttributeRecipeDao = new RecipeAttributeRecipeDao(dslContext.configuration());
    }

    @Override
    public Optional<RecipeFullEntity> fetchById(Long id) {
        return basicFullEntitySelect()
                .where(recipesTable.ID.eq(id))
                .fetch()
                .intoGroups(recipesTable.fields())
                .entrySet()
                .stream()
                .map(e -> mapRecordToFullEntity(e.getKey(), e.getValue()))
                .findFirst();
    }

    @Override
    public List<RecipeFullEntity> list(Integer offset, Integer limit) {
        return basicFullEntitySelect()
                .offset(offset)
                .limit(limit)
                .fetch()
                .intoGroups(recipesTable.fields())
                .entrySet()
                .stream()
                .map(e -> mapRecordToFullEntity(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(recipesTable).where(recipesTable.ID.eq(id)).execute() > 0;
    }

    @Override
    public Optional<RecipeEntity> update(Long id, RecipeEntity entity) {
        return dslContext.update(recipesTable)
                .set(recipesTable.CALORIES, entity.getCalories())
                .set(recipesTable.DESCRIPTION, entity.getDescription())
                .set(recipesTable.CARBOHYDRATES, entity.getCarbohydrates())
                .set(recipesTable.DRAFT, entity.getDraft())
                .set(recipesTable.FATS, entity.getFats())
                .set(recipesTable.TITLE, entity.getTitle())
                .set(recipesTable.PROTEINS, entity.getProteins())
                .set(recipesTable.UPDATED_AT, TimeHelper.currentTime())
                .where(recipesTable.ID.eq(id))
                .returning()
                .fetchOptionalInto(RecipeEntity.class);
    }

    @Override
    public Optional<RecipeEntity> create(RecipeEntity entity) {
        return dslContext.insertInto(recipesTable)
                .set(recipesTable.CALORIES, entity.getCalories())
                .set(recipesTable.DESCRIPTION, entity.getDescription())
                .set(recipesTable.CARBOHYDRATES, entity.getCarbohydrates())
                .set(recipesTable.DRAFT, entity.getDraft())
                .set(recipesTable.FATS, entity.getFats())
                .set(recipesTable.TITLE, entity.getTitle())
                .set(recipesTable.PROTEINS, entity.getProteins())
                .set(recipesTable.UPDATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(RecipeEntity.class);
    }

    @Override
    public void bindRecipeToRecipeAttributes(List<RecipeAttributeRecipe> entities) {
        recipeAttributeRecipeDao.merge(entities);
    }

    private RecipeFullEntity mapRecordToFullEntity(Record key, Result<?> r) {
        var recipeRec = key.into(recipesTable.fields()).into(RecipeFullEntity.class);
        var attributesRecs = r.into(recipeAttributesTable.fields()).into(RecipeAttributeEntity.class);
        var goods = r.into(goodsTable.fields()).into(GoodsEntity.class);
        recipeRec.setAttributes(attributesRecs);
        recipeRec.setGoods(goods);
        return recipeRec;
    }

    private SelectJoinStep<?> basicFullEntitySelect() {
        return dslContext.select().from(recipesTable)
                .leftJoin(recAttrRecTable).on(recAttrRecTable.RECIPE_ID.eq(recipesTable.ID))
                .leftJoin(goodsItemRecipeTable).on(goodsItemRecipeTable.RECIPE_ID.eq(recipesTable.ID))
                .leftJoin(recipeAttributesTable).on(recAttrRecTable.RECIPE_ATTRIBUTE_ID.eq(recipeAttributesTable.ID))
                .leftJoin(goodsTable).on(goodsItemRecipeTable.GOODS_ITEM_ID.eq(goodsTable.ID));
    }
}
