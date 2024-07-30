package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.entity.FavoriteCategory;
import ru.sparural.engine.entity.FavoriteCategoryUserEntity;
import ru.sparural.engine.repositories.FavoriteCategoriesRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.FavoriteCategories;
import ru.sparural.tables.FavoriteCategoryMonths;
import ru.sparural.tables.FavoriteCategoryUser;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class FavoriteCategoriesRepositoryImpl implements FavoriteCategoriesRepository {
    private final DSLContext dslContext;

    @Override
    public Optional<FavoriteCategory> get(Long id) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id))
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Optional<FavoriteCategory> get(Long userId, Long id) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCETYPE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCEVALUE)
                .from(FavoriteCategories.FAVORITE_CATEGORIES.leftJoin(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                        .on(FavoriteCategories.FAVORITE_CATEGORIES.ID
                                .eq(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITECATEGORYID)))
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID.eq(userId)
                        .and(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id)))
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Optional<FavoriteCategory> update(Long id, FavoriteCategoriesDataRequestDto dto) {
        dslContext
                .update(FavoriteCategories.FAVORITE_CATEGORIES)
                .set(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC, coalesce(val(dto.getIsPublic()), FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC))
                .set(FavoriteCategories.FAVORITE_CATEGORIES.UPDATEDAT, new Date().getTime())
                .where(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id))
                .returning()
                .fetch();

        return get(id);
    }

    @Override
    public List<FavoriteCategory> list(Integer offset, Integer limit) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .orderBy(FavoriteCategories.FAVORITE_CATEGORIES.CREATEDAT.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(FavoriteCategory.class);
    }

    @Override
    public List<FavoriteCategory> list(Long userId, Integer offset, Integer limit) {
        var time = new Date().getTime() / 1000;

        return dslContext.select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCETYPE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCEVALUE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ACCEPTED)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.STARTACTIVEDATE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ENDACTIVEDATE)
                .from(FavoriteCategories.FAVORITE_CATEGORIES.leftJoin(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                        .on(FavoriteCategories.FAVORITE_CATEGORIES.ID
                                .eq(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITECATEGORYID)))
                .leftJoin(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .on(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTHID
                        .eq(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID))
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID.eq(userId)
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH
                                .eq(LocalDate.now().getMonthValue())
                                .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR
                                        .eq(LocalDate.now().getYear())))
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.STARTACTIVEDATE.lessOrEqual(time))
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ENDACTIVEDATE.greaterOrEqual(time))
                        .and(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC))
                .orderBy(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.CREATEDAT)
                .offset(offset)
                .limit(limit)
                .fetchInto(FavoriteCategory.class);
    }

    @Override
    public Optional<Long> findByGoodsGroupUID(String goodsGroupUID) {
        return dslContext.select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.GOODSGROUPUID.eq(goodsGroupUID))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public void createAsync(FavoriteCategory entity) {
        dslContext.insertInto(FavoriteCategories.FAVORITE_CATEGORIES)
                .set(FavoriteCategories.FAVORITE_CATEGORIES.NAME, entity.getName())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.GOODSGROUPUID, entity.getGoodsGroupUID())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.ISPUBLIC, entity.getIsPublic())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAXID, entity.getLoymaxId())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.CREATEDAT, new Date().getTime())
                .onConflict(FavoriteCategories.FAVORITE_CATEGORIES.GOODSGROUPUID)
                .doUpdate()
                .set(FavoriteCategories.FAVORITE_CATEGORIES.NAME, entity.getName())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAXID, entity.getLoymaxId())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.UPDATEDAT, new Date().getTime())
                .executeAsync();
    }


    @Override
    public Optional<Long> getByMonthAndYear(Long userId, Integer currentYear, Integer currentMonth) {
        return dslContext.select(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID)
                .from(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .where(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR.eq(currentYear)
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH.eq(currentMonth))
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.USERID.eq(userId)))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Long createMonthAndYear(Long userId, Integer currentYear, Integer currentMonth) {
        return dslContext.insertInto(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.USERID, userId)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH, currentMonth)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR, currentYear)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.CREATEDAT, new Date().getTime())
                .returningResult()
                .fetchOne(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID);
    }

    @Override
    public Optional<FavoriteCategory> createCategoryUser(FavoriteCategoryUserEntity favoriteCategoryUser) {
        return dslContext.insertInto(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID, favoriteCategoryUser.getUserId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTHID, favoriteCategoryUser.getCategoryMonthId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITECATEGORYID, favoriteCategoryUser.getCategoryId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ACCEPTED, favoriteCategoryUser.getAccepted())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.STARTACTIVEDATE, favoriteCategoryUser.getStartActiveDate())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ENDACTIVEDATE, favoriteCategoryUser.getEndActiveDate())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCETYPE, favoriteCategoryUser.getPreferenceType())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCEVALUE, favoriteCategoryUser.getPreferenceValue())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.CREATEDAT, new Date().getTime())
                .returningResult()
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Long findCategoriesByUserIdAndMonthId(Long userId, Long categoryMonthId) {
        return dslContext.select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ID)
                .from(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID.eq(userId)
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTHID.eq(categoryMonthId)))
                .stream().count();
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext.delete(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public void deleteAsyncByUserId(Long userId) {
        dslContext.delete(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID.eq(userId))
                .executeAsync();
    }

    @Override
    public void deleteByUserId(Long userId) {
        dslContext.delete(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USERID.eq(userId))
                .execute();
    }

    @Override
    public Optional<String> findByIdWithGroupId(Long id) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAXID)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id))
                .fetchOptionalInto(String.class);
    }

    @Override
    public void batchCreateCategoryAsync(List<FavoriteCategory> categoriesListEntity) {
        var table = FavoriteCategories.FAVORITE_CATEGORIES;
        var insert = dslContext
                .insertInto(
                        table,
                        table.GOODSGROUPUID,
                        table.NAME,
                        table.ISPUBLIC,
                        table.LOYMAXID,
                        table.CREATEDAT
                );

        for (var entity : categoriesListEntity)
            insert = insert.values(
                    entity.getGoodsGroupUID(),
                    entity.getName(),
                    entity.getIsPublic(),
                    entity.getLoymaxId(),
                    TimeHelper.currentTime()
            );

        insert
                .onConflict(FavoriteCategories.FAVORITE_CATEGORIES.GOODSGROUPUID)
                .doUpdate()
                .set(table.NAME, DSL.coalesce(table.as("excluded").NAME, table.NAME))
                .set(table.LOYMAXID, DSL.coalesce(table.as("excluded").LOYMAXID, table.LOYMAXID))
                .set(table.UPDATEDAT, new Date().getTime())
                .executeAsync();
    }

    @Override
    public void batchCreateCategoryUser(List<FavoriteCategoryUserEntity> collect) {
        var table = FavoriteCategoryUser.FAVORITE_CATEGORY_USER;
        var insert =
                dslContext
                        .insertInto(
                                table,
                                table.USERID,
                                table.MONTHID,
                                table.FAVORITECATEGORYID,
                                table.ACCEPTED,
                                table.STARTACTIVEDATE,
                                table.ENDACTIVEDATE,
                                table.PREFERENCETYPE,
                                table.PREFERENCEVALUE,
                                table.CREATEDAT
                        );

        for (var entity : collect)
            insert = insert.values(
                    entity.getUserId(),
                    entity.getCategoryMonthId(),
                    entity.getCategoryId(),
                    entity.getAccepted(),
                    entity.getStartActiveDate(),
                    entity.getEndActiveDate(),
                    entity.getPreferenceType(),
                    entity.getPreferenceValue(),
                    TimeHelper.currentTime()
            );

        insert.onConflict(table.USERID, table.FAVORITECATEGORYID)
                .doUpdate()
                .set(table.ENDACTIVEDATE, DSL.coalesce(table.as("excluded").ENDACTIVEDATE, table.ENDACTIVEDATE))
                .set(table.STARTACTIVEDATE, DSL.coalesce(table.as("excluded").STARTACTIVEDATE, table.STARTACTIVEDATE))
                .set(table.ACCEPTED, DSL.coalesce(table.as("excluded").ACCEPTED, table.ACCEPTED))
                .set(table.MONTHID, DSL.coalesce(table.as("excluded").MONTHID, table.MONTHID))
                .set(table.PREFERENCETYPE, DSL.coalesce(table.as("excluded").PREFERENCETYPE, table.PREFERENCETYPE))
                .set(table.PREFERENCEVALUE, DSL.coalesce(table.as("excluded").PREFERENCEVALUE, table.PREFERENCEVALUE))
                .set(table.UPDATEDAT, new Date().getTime())
                .execute();
    }

}