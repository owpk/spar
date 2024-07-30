package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.entity.RecipeAttributeEntity;
import ru.sparural.engine.entity.RecipeEntity;
import ru.sparural.engine.entity.RecipeFullEntity;
import ru.sparural.engine.repositories.RecipeRepository;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
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

    private static final String LIMIT_RECIPES_SUBTABLE="limit_recipes_tbl";

    private final DSLContext dslContext;
    private final Recipes recipesTable = Recipes.RECIPES;
    private final RecipeAttributes recipeAttributesTable =
            RecipeAttributes.RECIPE_ATTRIBUTES;
    private final ru.sparural.tables.RecipeAttributeRecipe recAttrRecTable =
            ru.sparural.tables.RecipeAttributeRecipe.RECIPE_ATTRIBUTE_RECIPE;
    private final GoodsItemRecipe goodsItemRecipeTable =
            GoodsItemRecipe.GOODS_ITEM_RECIPE;
    private final Goods goodsTable = Goods.GOODS;
    private final FileDocumentService fileDocumentService;

    private RecipeAttributeRecipeDao recipeAttributeRecipeDao;

    @PostConstruct
    private void init() {
        recipeAttributeRecipeDao = new RecipeAttributeRecipeDao(dslContext.configuration());
    }

    @Override
    public RecipeFullEntity fetchById(Long id) {
        try(var select = dslContext.selectFrom(recipesTable)) {
            RecipeFullEntity recipeFullEntity = select
                    .where(recipesTable.ID.eq(id))
                    .fetchOptionalInto(RecipeFullEntity.class)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

            recipeFullEntity.getAttributes().addAll(this.getAttributesByRecipeId(recipeFullEntity.getId()));
            recipeFullEntity.getGoods().addAll(this.getGoodsByRecipeId(recipeFullEntity.getId()));

            return recipeFullEntity;
        }
    }

    @Override
    public List<RecipeFullEntity> list(Integer offset, Integer limit) {
        try(var select = dslContext.selectFrom(recipesTable)) {
            return select
                    .orderBy(recipesTable.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetchInto(RecipeFullEntity.class);
        }
    }

    private RecipeFullEntity mapRecordToRecipe(Record r) {
        return r.into(recipesTable.as(LIMIT_RECIPES_SUBTABLE).fields()).into(RecipeFullEntity.class);
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
    @Transactional
    public RecipeEntity update(
            Long recipeId,
            RecipeEntity recipe,
            List<Long> goodsIds,
            List<Long> attributesIds
    ) {
        RecipeEntity recipeEntity = this.update(recipeId, recipe)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update recipe: " + recipe));

        bindGoods(recipeEntity.getId(), goodsIds);
        bindAttributes(recipeEntity.getId(), attributesIds);

        return recipeEntity;
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
    @Transactional
    public RecipeEntity create(
            RecipeEntity recipe,
            List<Long> goodsIds,
            List<Long> attributesIds
    ) {
        RecipeEntity recipeEntity = this.create(recipe)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create recipe: " + recipe));

        bindGoods(recipeEntity.getId(), goodsIds);
        bindAttributes(recipeEntity.getId(), attributesIds);

        return recipeEntity;
    }


    public List<Long> getAttributesIdByRecipeId(Long recipeId) {
        return dslContext.select(recAttrRecTable.RECIPE_ATTRIBUTE_ID)
                .from(recAttrRecTable)
                .where(recAttrRecTable.RECIPE_ID.eq(recipeId))
                .fetch(recAttrRecTable.RECIPE_ATTRIBUTE_ID);
    }

    @Override
    public List<RecipeAttributeEntity> getAttributesByRecipeId(Long recipeId) {
        return dslContext.select(recipeAttributesTable.asterisk())
                .from(recAttrRecTable)
                .join(recipeAttributesTable)
                .on(recAttrRecTable.RECIPE_ATTRIBUTE_ID
                        .eq(recipeAttributesTable.ID))
                .where(recAttrRecTable.RECIPE_ID.eq(recipeId))
                .fetchInto(RecipeAttributeEntity.class);
    }

    public void insertAttributesIntoRecipeById(Long recipeId, List<Long> attributeIds) {
        List<Query> collect = attributeIds.stream().map(recAttrId ->
                dslContext.insertInto(recAttrRecTable)
                        .set(recAttrRecTable.RECIPE_ID, recipeId)
                        .set(recAttrRecTable.RECIPE_ATTRIBUTE_ID, recAttrId)
                        .set(recAttrRecTable.CREATED_AT, TimeHelper.currentTime())
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }

    public void deleteAttributesFromRecipeById(Long recipeId, List<Long> attributeIds) {
        List<Query> collect = attributeIds.stream().map(recAttrId ->
                dslContext.deleteFrom(recAttrRecTable)
                        .where(recAttrRecTable.RECIPE_ATTRIBUTE_ID.eq(recAttrId))
                        .and(recAttrRecTable.RECIPE_ID.eq(recipeId))
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }

    public List<Long> getGoodsIdsByRecipeId(Long recipeId) {
        return dslContext.select(goodsItemRecipeTable.GOODS_ITEM_ID)
                .from(goodsItemRecipeTable)
                .where(goodsItemRecipeTable.RECIPE_ID.eq(recipeId))
                .fetch(goodsItemRecipeTable.GOODS_ITEM_ID);
    }

    @Override
    public List<GoodsEntity> getGoodsByRecipeId(Long recipeId) {
        return dslContext.select(goodsTable.asterisk())
                .from(goodsItemRecipeTable)
                .join(goodsTable).on(goodsItemRecipeTable.GOODS_ITEM_ID.eq(goodsTable.ID))
                .where(goodsItemRecipeTable.RECIPE_ID.eq(recipeId))
                .fetchInto(GoodsEntity.class);
    }

    public void insertGoodsIntoRecipeById(Long recipeId, List<Long> goodsIds) {
        List<Query> collect = goodsIds.stream().map(goodId ->
                dslContext.insertInto(goodsItemRecipeTable)
                        .set(goodsItemRecipeTable.RECIPE_ID, recipeId)
                        .set(goodsItemRecipeTable.GOODS_ITEM_ID, goodId)
                        .set(goodsItemRecipeTable.CREATED_AT, TimeHelper.currentTime())
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }

    public void deleteGoodsFromRecipeById(Long recipeId, List<Long> goodsIds) {
        List<Query> collect = goodsIds.stream().map(goodsAttrId ->
                dslContext.deleteFrom(goodsItemRecipeTable)
                        .where(goodsItemRecipeTable.GOODS_ITEM_ID.eq(goodsAttrId))
                        .and(goodsItemRecipeTable.RECIPE_ID.eq(recipeId))
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }

    private void bindAttributes(Long recipeId, List<Long> attributeIds) {
        List<Long> allFromDB = this.getAttributesIdByRecipeId(recipeId);

        var forDelete = allFromDB.stream().filter(id -> !attributeIds.contains(id)).collect(Collectors.toList());
        var forInsert = attributeIds.stream().filter(id -> !allFromDB.contains(id)).collect(Collectors.toList());

        if(!forDelete.isEmpty())
            this.deleteAttributesFromRecipeById(recipeId, forDelete);

        if(!forInsert.isEmpty())
            this.insertAttributesIntoRecipeById(recipeId, forInsert);
    }

    private void bindGoods(Long recipeId, List<Long> goodIds) {
        List<Long> allFromDB = this.getGoodsIdsByRecipeId(recipeId);

        var forDelete = allFromDB.stream().filter(id -> !goodIds.contains(id)).collect(Collectors.toList());
        var forInsert = goodIds.stream().filter(id -> !allFromDB.contains(id)).collect(Collectors.toList());

        if(!forDelete.isEmpty())
            this.deleteGoodsFromRecipeById(recipeId, forDelete);

        if(!forInsert.isEmpty())
            this.insertGoodsIntoRecipeById(recipeId, forInsert);
    }

    @Deprecated
    @Override
    public void bindRecipeToRecipeAttributes(List<RecipeAttributeRecipe> entities) {
        recipeAttributeRecipeDao.merge(entities);
    }
}
