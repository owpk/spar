package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.repositories.GoodsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.Goods;
import ru.sparural.tables.GoodsItemRecipe;
import ru.sparural.tables.daos.GoodsItemRecipeDao;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class GoodsRepositoryImpl implements GoodsRepository {

    private final DSLContext dslContext;
    private final Goods table = Goods.GOODS;
    private final GoodsItemRecipe bridgeTable = GoodsItemRecipe.GOODS_ITEM_RECIPE;
    private GoodsItemRecipeDao goodsItemRecipeDao;

    @PostConstruct
    private void init() {
        goodsItemRecipeDao = new GoodsItemRecipeDao(dslContext.configuration());
    }

    @Override
    public List<GoodsEntity> fetch(int offset, int limit, String search) {
        var defaultCondition = table.CREATED_AT.notEqual(table.UPDATED_AT);
        Function<String, String> func = (str) -> String.format("%%%s%%", str);
        if (search != null)
            defaultCondition = defaultCondition.and(
                    table.EXT_GOODS_ID.eq(search)
                            .or(table.NAME.likeIgnoreCase(func.apply(search)))
                            .or(table.DESCRIPTION.likeIgnoreCase(func.apply(search))));
        return dslContext.selectFrom(table)
                .where(defaultCondition)
                .orderBy(table.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch().into(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> get(Long id) {
        return dslContext
                .selectFrom(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> get(String goodsId) {
        return dslContext
                .selectFrom(table)
                .where(table.EXT_GOODS_ID.eq(goodsId))
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> findByGoodsId(Long id, String goodsId) {
        return dslContext
                .selectFrom(table)
                .where(table.EXT_GOODS_ID.eq(goodsId)
                        .and(table.ID.notEqual(id)))
                .limit(1)
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> create(GoodsEntity goodsEntity) {
        return dslContext
                .insertInto(table)
                .set(table.EXT_GOODS_ID, goodsEntity.getExtGoodsId())
                .set(table.NAME, goodsEntity.getName())
                .set(table.DESCRIPTION, goodsEntity.getDescription())
                .set(table.DRAFT, goodsEntity.getDraft() == null || goodsEntity.getDraft())
                .set(table.CREATED_AT, new Date().getTime())
                .set(table.UPDATED_AT, new Date().getTime())
                .returning()
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> update(Long id, GoodsEntity goodsEntity) {

        return dslContext.update(table)
                .set(table.EXT_GOODS_ID, coalesce(val(goodsEntity.getExtGoodsId()), table.EXT_GOODS_ID))
                .set(table.NAME, coalesce(val(goodsEntity.getName()), table.NAME))
                .set(table.DESCRIPTION, coalesce(val(goodsEntity.getDescription()), table.DESCRIPTION))
                .set(table.DRAFT, coalesce(val(goodsEntity.getDraft()), table.DRAFT))
                .set(table.UPDATED_AT, new Date().getTime())
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> update(String goodsId, GoodsEntity goodsEntity) {
        return dslContext.update(table)
                .set(table.EXT_GOODS_ID, coalesce(val(goodsEntity.getExtGoodsId()), table.EXT_GOODS_ID))
                .set(table.NAME, coalesce(val(goodsEntity.getName()), table.NAME))
                .set(table.DESCRIPTION, coalesce(val(goodsEntity.getDescription()), table.DESCRIPTION))
                .set(table.DRAFT, coalesce(val(goodsEntity.getDraft()), table.DRAFT))
                .set(table.UPDATED_AT, new Date().getTime())
                .where(table.EXT_GOODS_ID.eq(goodsId))
                .returning()
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(table)
                .where(table.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public Boolean delete(String id) {
        return dslContext.delete(table)
                .where(table.EXT_GOODS_ID.eq(id))
                .execute() == 1;
    }

    @Override
    public List<GoodsEntity> fetchAllByLoymaxIds(List<String> extIds) {
        return dslContext.selectFrom(table)
                .where(table.EXT_GOODS_ID.in(extIds))
                .fetchInto(GoodsEntity.class);
    }

    @Override
    public List<Long> getAllRecipeGoodIdByRecipeId(Long recipeId) {
        return dslContext.select(bridgeTable.GOODS_ITEM_ID)
                .from(bridgeTable)
                .where(bridgeTable.RECIPE_ID.eq(recipeId))
                .fetch(bridgeTable.GOODS_ITEM_ID);
    }

    @Override
    public void deleteAllFromBridge(Long recipeId, List<Long> forDelete) {
        List<Query> collect = forDelete.stream().map(recAttrId ->
                dslContext.deleteFrom(bridgeTable)
                        .where(bridgeTable.GOODS_ITEM_ID.eq(recAttrId))
                        .and(bridgeTable.RECIPE_ID.eq(recipeId))
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }

    @Override
    public void insertIntoBridge(Long recipeId, List<Long> forInsert) {
        List<Query> collect = forInsert.stream().map(recAttrId ->
                dslContext.insertInto(bridgeTable)
                        .set(bridgeTable.RECIPE_ID, recipeId)
                        .set(bridgeTable.GOODS_ITEM_ID, recAttrId)
                        .set(bridgeTable.CREATED_AT, TimeHelper.currentTime())
        ).collect(Collectors.toList());

        dslContext.batch(collect).execute();
    }
}
