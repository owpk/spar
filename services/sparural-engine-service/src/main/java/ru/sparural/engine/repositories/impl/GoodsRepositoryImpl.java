package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.repositories.GoodsRepository;
import ru.sparural.tables.Goods;
import ru.sparural.tables.daos.GoodsItemRecipeDao;
import ru.sparural.tables.pojos.GoodsItemRecipe;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class GoodsRepositoryImpl implements GoodsRepository {

    private final DSLContext dslContext;
    private final Goods table = Goods.GOODS;
    private GoodsItemRecipeDao goodsItemRecipeDao;

    @PostConstruct
    private void init() {
        goodsItemRecipeDao = new GoodsItemRecipeDao(dslContext.configuration());
    }

    @Override
    public List<GoodsEntity> fetch(int offset, int limit, String search) {
        var defaultCondition = table.CREATEDAT.notEqual(table.UPDATEDAT);
        Function<String, String> func = (str) -> String.format("%%%s%%", str);
        if (search != null)
            defaultCondition = defaultCondition.and(
                    table.EXTGOODSID.eq(search)
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
                .where(table.EXTGOODSID.eq(goodsId))
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> findByGoodsId(Long id, String goodsId) {
        return dslContext
                .selectFrom(table)
                .where(table.EXTGOODSID.eq(goodsId)
                        .and(table.ID.notEqual(id)))
                .limit(1)
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> create(GoodsEntity goodsEntity) {
        return dslContext
                .insertInto(table)
                .set(table.EXTGOODSID, goodsEntity.getExtGoodsId())
                .set(table.NAME, goodsEntity.getName())
                .set(table.DESCRIPTION, goodsEntity.getDescription())
                .set(table.DRAFT, goodsEntity.getDraft() == null || goodsEntity.getDraft())
                .set(table.CREATEDAT, new Date().getTime())
                .set(table.UPDATEDAT, new Date().getTime())
                .returning()
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> update(Long id, GoodsEntity goodsEntity) {

        return dslContext.update(table)
                .set(table.EXTGOODSID, coalesce(val(goodsEntity.getExtGoodsId()), table.EXTGOODSID))
                .set(table.NAME, coalesce(val(goodsEntity.getName()), table.NAME))
                .set(table.DESCRIPTION, coalesce(val(goodsEntity.getDescription()), table.DESCRIPTION))
                .set(table.DRAFT, coalesce(val(goodsEntity.getDraft()), table.DRAFT))
                .set(table.UPDATEDAT, new Date().getTime())
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(GoodsEntity.class);
    }

    @Override
    public Optional<GoodsEntity> update(String goodsId, GoodsEntity goodsEntity) {
        return dslContext.update(table)
                .set(table.EXTGOODSID, coalesce(val(goodsEntity.getExtGoodsId()), table.EXTGOODSID))
                .set(table.NAME, coalesce(val(goodsEntity.getName()), table.NAME))
                .set(table.DESCRIPTION, coalesce(val(goodsEntity.getDescription()), table.DESCRIPTION))
                .set(table.DRAFT, coalesce(val(goodsEntity.getDraft()), table.DRAFT))
                .set(table.UPDATEDAT, new Date().getTime())
                .where(table.EXTGOODSID.eq(goodsId))
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
                .where(table.EXTGOODSID.eq(id))
                .execute() == 1;
    }

    @Override
    public void bindGoodsToRecipe(List<GoodsItemRecipe> entities) {
        goodsItemRecipeDao.merge(entities);
    }
}
