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

import static org.jooq.impl.DSL.coalesce;
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
                .select(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id))
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Optional<FavoriteCategory> get(Long userId, Long id) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_TYPE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_VALUE)
                .from(FavoriteCategories.FAVORITE_CATEGORIES.leftJoin(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                        .on(FavoriteCategories.FAVORITE_CATEGORIES.ID
                                .eq(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITE_CATEGORY_ID)))
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID.eq(userId)
                        .and(FavoriteCategories.FAVORITE_CATEGORIES.ID.eq(id)))
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Optional<FavoriteCategory> update(Long id, FavoriteCategoriesDataRequestDto dto) {
        dslContext
                .update(FavoriteCategories.FAVORITE_CATEGORIES)
                .set(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC, coalesce(val(dto.getIsPublic()), FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC))
                .set(FavoriteCategories.FAVORITE_CATEGORIES.UPDATED_AT, new Date().getTime())
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
                .select(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .orderBy(FavoriteCategories.FAVORITE_CATEGORIES.CREATED_AT.desc())
                .offset(offset)
                .limit(limit)
                .fetch()
                .into(FavoriteCategory.class);
    }

    @Override
    public List<FavoriteCategory> list(Long userId, Integer offset, Integer limit) {
        var time = new Date().getTime() / 1000;

        return dslContext.select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_TYPE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_VALUE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ACCEPTED)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.NAME)
                .select(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.START_ACTIVE_DATE)
                .select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.END_ACTIVE_DATE)
                .from(FavoriteCategories.FAVORITE_CATEGORIES.leftJoin(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                        .on(FavoriteCategories.FAVORITE_CATEGORIES.ID
                                .eq(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITE_CATEGORY_ID)))
                .leftJoin(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .on(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTH_ID
                        .eq(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID))
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID.eq(userId)
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH
                                .eq(LocalDate.now().getMonthValue())
                                .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR
                                        .eq(LocalDate.now().getYear())))
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.START_ACTIVE_DATE.lessOrEqual(time))
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.END_ACTIVE_DATE.greaterOrEqual(time))
                        .and(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC))
                .orderBy(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.CREATED_AT)
                .offset(offset)
                .limit(limit)
                .fetchInto(FavoriteCategory.class);
    }

    @Override
    public Optional<Long> findByGoodsGroupUID(String goodsGroupUID) {
        return dslContext.select(FavoriteCategories.FAVORITE_CATEGORIES.ID)
                .from(FavoriteCategories.FAVORITE_CATEGORIES)
                .where(FavoriteCategories.FAVORITE_CATEGORIES.GOODS_GROUP_U_I_D.eq(goodsGroupUID))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public void createAsync(FavoriteCategory entity) {
        dslContext.insertInto(FavoriteCategories.FAVORITE_CATEGORIES)
                .set(FavoriteCategories.FAVORITE_CATEGORIES.NAME, entity.getName())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.GOODS_GROUP_U_I_D, entity.getGoodsGroupUID())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.IS_PUBLIC, entity.getIsPublic())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAX_ID, entity.getLoymaxId())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.CREATED_AT, new Date().getTime())
                .onConflict(FavoriteCategories.FAVORITE_CATEGORIES.GOODS_GROUP_U_I_D)
                .doUpdate()
                .set(FavoriteCategories.FAVORITE_CATEGORIES.NAME, entity.getName())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAX_ID, entity.getLoymaxId())
                .set(FavoriteCategories.FAVORITE_CATEGORIES.UPDATED_AT, new Date().getTime())
                .executeAsync();
    }


    @Override
    public Optional<Long> getByMonthAndYear(Long userId, Integer currentYear, Integer currentMonth) {
        return dslContext.select(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID)
                .from(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .where(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR.eq(currentYear)
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH.eq(currentMonth))
                        .and(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.USER_ID.eq(userId)))
                .fetchOptionalInto(Long.class);
    }

    @Override
    public Long createMonthAndYear(Long userId, Integer currentYear, Integer currentMonth) {
        return dslContext.insertInto(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.USER_ID, userId)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.MONTH, currentMonth)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.YEAR, currentYear)
                .set(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.CREATED_AT, new Date().getTime())
                .returningResult()
                .fetchOne(FavoriteCategoryMonths.FAVORITE_CATEGORY_MONTHS.ID);
    }

    @Override
    public Optional<FavoriteCategory> createCategoryUser(FavoriteCategoryUserEntity favoriteCategoryUser) {
        return dslContext.insertInto(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID, favoriteCategoryUser.getUserId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTH_ID, favoriteCategoryUser.getCategoryMonthId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.FAVORITE_CATEGORY_ID, favoriteCategoryUser.getCategoryId())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ACCEPTED, favoriteCategoryUser.getAccepted())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.START_ACTIVE_DATE, favoriteCategoryUser.getStartActiveDate())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.END_ACTIVE_DATE, favoriteCategoryUser.getEndActiveDate())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_TYPE, favoriteCategoryUser.getPreferenceType())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.PREFERENCE_VALUE, favoriteCategoryUser.getPreferenceValue())
                .set(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.CREATED_AT, new Date().getTime())
                .returningResult()
                .fetchOptionalInto(FavoriteCategory.class);
    }

    @Override
    public Long findCategoriesByUserIdAndMonthId(Long userId, Long categoryMonthId) {
        return dslContext.select(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.ID)
                .from(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID.eq(userId)
                        .and(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.MONTH_ID.eq(categoryMonthId)))
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
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID.eq(userId))
                .executeAsync();
    }

    @Override
    public void deleteByUserId(Long userId) {
        dslContext.delete(FavoriteCategoryUser.FAVORITE_CATEGORY_USER)
                .where(FavoriteCategoryUser.FAVORITE_CATEGORY_USER.USER_ID.eq(userId))
                .execute();
    }

    @Override
    public Optional<String> findByIdWithGroupId(Long id) {
        return dslContext
                .select(FavoriteCategories.FAVORITE_CATEGORIES.LOYMAX_ID)
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
                        table.GOODS_GROUP_U_I_D,
                        table.NAME,
                        table.IS_PUBLIC,
                        table.LOYMAX_ID,
                        table.CREATED_AT
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
                .onConflict(FavoriteCategories.FAVORITE_CATEGORIES.GOODS_GROUP_U_I_D)
                .doUpdate()
                .set(table.NAME, DSL.coalesce(table.as("excluded").NAME, table.NAME))
                .set(table.LOYMAX_ID, DSL.coalesce(table.as("excluded").LOYMAX_ID, table.LOYMAX_ID))
                .set(table.UPDATED_AT, new Date().getTime())
                .executeAsync();
    }

    @Override
    public void batchCreateCategoryUser(List<FavoriteCategoryUserEntity> collect) {
        var table = FavoriteCategoryUser.FAVORITE_CATEGORY_USER;
        var insert =
                dslContext
                        .insertInto(
                                table,
                                table.USER_ID,
                                table.MONTH_ID,
                                table.FAVORITE_CATEGORY_ID,
                                table.ACCEPTED,
                                table.START_ACTIVE_DATE,
                                table.END_ACTIVE_DATE,
                                table.PREFERENCE_TYPE,
                                table.PREFERENCE_VALUE,
                                table.CREATED_AT
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

        insert.onConflict(table.USER_ID, table.FAVORITE_CATEGORY_ID)
                .doUpdate()
                .set(table.END_ACTIVE_DATE, DSL.coalesce(table.as("excluded").END_ACTIVE_DATE, table.END_ACTIVE_DATE))
                .set(table.START_ACTIVE_DATE, DSL.coalesce(table.as("excluded").START_ACTIVE_DATE, table.START_ACTIVE_DATE))
                .set(table.ACCEPTED, DSL.coalesce(table.as("excluded").ACCEPTED, table.ACCEPTED))
                .set(table.MONTH_ID, DSL.coalesce(table.as("excluded").MONTH_ID, table.MONTH_ID))
                .set(table.PREFERENCE_TYPE, DSL.coalesce(table.as("excluded").PREFERENCE_TYPE, table.PREFERENCE_TYPE))
                .set(table.PREFERENCE_VALUE, DSL.coalesce(table.as("excluded").PREFERENCE_VALUE, table.PREFERENCE_VALUE))
                .set(table.UPDATED_AT, new Date().getTime())
                .execute();
    }

}