package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.entity.OnboxBanner;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.repositories.OnboxBannersRepository;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.CitySelectValues;
import ru.sparural.tables.Cities;
import ru.sparural.tables.CityOnboxBanner;
import ru.sparural.tables.OnboxBanners;
import ru.sparural.tables.Screens;
import ru.sparural.tables.records.OnboxBannersRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.jsonArrayAgg;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.val;

@Service
@RequiredArgsConstructor
public class OnboxBannersRepositoryImpl implements OnboxBannersRepository {

    private final DSLContext dslContext;

    @Override
    @Transactional
    public Optional<OnboxBanner> create(OnboxBanner data) throws ValidationException {

        if (data.getCitySelect() == null)
            data.setCitySelect(CitySelectValues.Selection);

        var create = dslContext.insertInto(OnboxBanners.ONBOX_BANNERS)
                .set(OnboxBanners.ONBOX_BANNERS.ORDER, data.getOrder())
                .set(OnboxBanners.ONBOX_BANNERS.ISPUBLIC, data.getIsPublic())
                .set(OnboxBanners.ONBOX_BANNERS.DRAFT, data.getDraft())
                .set(OnboxBanners.ONBOX_BANNERS.TITLE, data.getTitle())
                .set(OnboxBanners.ONBOX_BANNERS.CITYSELECT, data.getCitySelect())
                .set(OnboxBanners.ONBOX_BANNERS.DESCRIPTION, data.getDescription())
                .set(OnboxBanners.ONBOX_BANNERS.URL, data.getUrl())
                .set(OnboxBanners.ONBOX_BANNERS.CREATEDAT, TimeHelper.currentTime())
                .set(OnboxBanners.ONBOX_BANNERS.DATE_START, data.getDateStart())
                .set(OnboxBanners.ONBOX_BANNERS.DATE_END, data.getDateEnd());

        var result = create.returning().fetchOne();
        return insertCitiesListAndReturn(result, data, false);
    }

    @Override
    public boolean delete(Long id) {
        return dslContext.delete(OnboxBanners.ONBOX_BANNERS)
                .where(OnboxBanners.ONBOX_BANNERS.ID.eq(id)).execute() == 1;
    }

    @Override
    @Transactional
    public Optional<OnboxBanner> update(Long id, OnboxBanner data) {
        var update = dslContext.update(OnboxBanners.ONBOX_BANNERS)
                .set(OnboxBanners.ONBOX_BANNERS.UPDATEDAT, TimeHelper.currentTime());

        if (data.getOrder() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.ORDER, data.getOrder());
        if (data.getIsPublic() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.ISPUBLIC, data.getIsPublic());
        if (data.getDraft() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.DRAFT, data.getDraft());
        if (data.getTitle() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.TITLE, data.getTitle());
        if (data.getDescription() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.DESCRIPTION, data.getDescription());
        if (data.getDateStart() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.DATE_START, data.getDateStart());
        if (data.getDateEnd() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.DATE_END, data.getDateEnd());


        if (data.getTitle() == null) {
            update.set(OnboxBanners.ONBOX_BANNERS.URL, coalesce(val(data.getUrl()), OnboxBanners.ONBOX_BANNERS.URL))
                    .set(OnboxBanners.ONBOX_BANNERS.SCREENID, coalesce(val(data.getMobileNavigateTarget().getId()), OnboxBanners.ONBOX_BANNERS.SCREENID));
        } else {
            update.set(OnboxBanners.ONBOX_BANNERS.URL, data.getUrl())
                    .set(OnboxBanners.ONBOX_BANNERS.SCREENID, data.getMobileNavigateTarget().getId());
        }

        if (data.getCitySelect() != null)
            update.set(OnboxBanners.ONBOX_BANNERS.CITYSELECT, CitySelectValues.valueOf(data.getCitySelect().toString()));

        var result = update.where(OnboxBanners.ONBOX_BANNERS.ID.eq(id))
                .returning().fetchOne();
        var delete = data.getCities() != null;
        return insertCitiesListAndReturn(result, data, delete);
    }

    @Override
    public Optional<OnboxBanner> get(Long id) {
        var result = dslContext
                .select()
                .from(OnboxBanners.ONBOX_BANNERS)
                .leftJoin(CityOnboxBanner.CITY_ONBOX_BANNER)
                .on(OnboxBanners.ONBOX_BANNERS.ID.eq(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID))
                .leftJoin(Cities.CITIES)
                .on(Cities.CITIES.ID.eq(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID))
                .leftJoin(Screens.SCREENS)
                .on(Screens.SCREENS.ID.eq(OnboxBanners.ONBOX_BANNERS.SCREENID))
                .where(OnboxBanners.ONBOX_BANNERS.ID.eq(id))
                .fetch();

        return Optional.ofNullable(mapRecordToEntity(result));
    }

    @Override
    public List<OnboxBanner> list(int offset, int limit, boolean isPublic, Long dateStart, Long dateEnd) {
        if (isPublic)
            return basicSelect()
                    .where(currentDateCondition()
                            .and(OnboxBanners.ONBOX_BANNERS.ISPUBLIC.eq(true))
                            .and((OnboxBanners.ONBOX_BANNERS.DRAFT.eq(false)
                                    .or(OnboxBanners.ONBOX_BANNERS.DRAFT.isNull()))))
                    .orderBy(OnboxBanners.ONBOX_BANNERS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch()
                    .intoGroups(OnboxBanners.ONBOX_BANNERS)
                    .values().stream()
                    .map(this::mapRecordToEntity)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        else
            return dslContext.select(
                            OnboxBanners.ONBOX_BANNERS.ID,
                            OnboxBanners.ONBOX_BANNERS.TITLE,
                            OnboxBanners.ONBOX_BANNERS.DESCRIPTION,
                            OnboxBanners.ONBOX_BANNERS.URL,
                            OnboxBanners.ONBOX_BANNERS.SCREENID,
                            OnboxBanners.ONBOX_BANNERS.DRAFT,
                            OnboxBanners.ONBOX_BANNERS.ISPUBLIC,
                            OnboxBanners.ONBOX_BANNERS.CITYSELECT,
                            OnboxBanners.ONBOX_BANNERS.ORDER,
                            OnboxBanners.ONBOX_BANNERS.DATE_START,
                            OnboxBanners.ONBOX_BANNERS.DATE_END,
                            Screens.SCREENS.ID,
                            Screens.SCREENS.NAME,
                            Screens.SCREENS.CODE,
                            field(
                                    select(jsonArrayAgg(jsonObject(Cities.CITIES.ID,
                                            Cities.CITIES.NAME,
                                            Cities.CITIES.TIMEZONE)))
                                            .from(Cities.CITIES)
                                            .join(CityOnboxBanner.CITY_ONBOX_BANNER)
                                            .on(Cities.CITIES.ID.eq(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID))
                                            .where(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID.eq(OnboxBanners.ONBOX_BANNERS.ID))
                            ).as("cities")
                    )
                    .from(OnboxBanners.ONBOX_BANNERS)
                    .leftJoin(Screens.SCREENS)
                    .on(Screens.SCREENS.ID.eq(OnboxBanners.ONBOX_BANNERS.SCREENID))
                    .where(dateCondition(dateStart, dateEnd).and(OnboxBanners.ONBOX_BANNERS.DRAFT.eq(false)
                            .or(OnboxBanners.ONBOX_BANNERS.DRAFT.isNull())))
                    .orderBy(OnboxBanners.ONBOX_BANNERS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch()
                    .intoGroups(OnboxBanners.ONBOX_BANNERS)
                    .values()
                    .stream()
                    .map(x -> {
                        if (x.get(0) == null)
                            return null;
                        OnboxBanner onboxBanner = x.get(0)
                                .into(OnboxBanners.ONBOX_BANNERS.fields())
                                .into(OnboxBanner.class);
                        List<City> cities = x
                                .into(Cities.CITIES.fields())
                                .into(City.class);

                        onboxBanner.setId(x.get(0).get(OnboxBanners.ONBOX_BANNERS.ID));

                        if (onboxBanner.getCitySelect().equals(CitySelectValues.Selection))
                            onboxBanner.setCities(cities);

                        if (onboxBanner.getCities() == null) {
                            onboxBanner.setCities(new ArrayList<>());
                        }
                        Screen screen = x.get(0)
                                .into(Screens.SCREENS)
                                .into(Screen.class);

                        onboxBanner.setMobileNavigateTarget(screen);
                        onboxBanner.setId(x.get(0).get(OnboxBanners.ONBOX_BANNERS.ID));

                        return onboxBanner;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    protected SelectOnConditionStep<Record> basicSelect() {
        return dslContext
                .select()
                .from(OnboxBanners.ONBOX_BANNERS)
                .leftJoin(CityOnboxBanner.CITY_ONBOX_BANNER)
                .on(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID
                        .eq(OnboxBanners.ONBOX_BANNERS.ID))
                .leftJoin(Cities.CITIES)
                .on(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID.eq(Cities.CITIES.ID))
                .leftJoin(Screens.SCREENS)
                .on(Screens.SCREENS.ID.eq(OnboxBanners.ONBOX_BANNERS.SCREENID));
    }

    private Condition dateCondition(Long dateStart, Long dateEnd) {
        var condition = DSL.noCondition();
        var table = OnboxBanners.ONBOX_BANNERS;
        if (dateStart != null)
            condition = condition.and(table.DATE_START.greaterOrEqual(dateStart));
        if (dateEnd != null)
            condition = condition.and(table.DATE_END.lessOrEqual(dateEnd));
        return condition;
    }

    private Condition currentDateCondition() {
        var currentTime = System.currentTimeMillis();
        var table = OnboxBanners.ONBOX_BANNERS;
        return table.DATE_START.lessOrEqual(currentTime).and(table.DATE_END.greaterThan(currentTime));
    }

    @Override
    public List<OnboxBanner> list(int offset, int limit, Long city, boolean isPublic, Long dateStart, Long dateEnd) {
        if (isPublic)
            return basicSelect()
                    .where(currentDateCondition().and(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID.eq(city)
                            .and((CityOnboxBanner.CITY_ONBOX_BANNER.onboxBanners().DRAFT.eq(false)
                                    .or(CityOnboxBanner.CITY_ONBOX_BANNER.onboxBanners().DRAFT.isNull())))))
                    .or(OnboxBanners.ONBOX_BANNERS.CITYSELECT.eq(CitySelectValues.All))
                    .and(OnboxBanners.ONBOX_BANNERS.DRAFT.eq(false))
                    .and(OnboxBanners.ONBOX_BANNERS.ISPUBLIC.eq(true))
                    .orderBy(OnboxBanners.ONBOX_BANNERS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch()
                    .intoGroups(OnboxBanners.ONBOX_BANNERS).values().stream()
                    .map(this::mapRecordToEntity)
                    .collect(Collectors.toList());
        else
            return basicSelect()
                    .where(dateCondition(dateStart, dateEnd).and(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID.eq(city)
                            .and((CityOnboxBanner.CITY_ONBOX_BANNER.onboxBanners().DRAFT.eq(false)
                                    .or(CityOnboxBanner.CITY_ONBOX_BANNER.onboxBanners().DRAFT.isNull())))))
                    .or(OnboxBanners.ONBOX_BANNERS.CITYSELECT.eq(CitySelectValues.All))
                    .and(OnboxBanners.ONBOX_BANNERS.DRAFT.eq(false))
                    .and(OnboxBanners.ONBOX_BANNERS.ISPUBLIC.eq(true))
                    .orderBy(OnboxBanners.ONBOX_BANNERS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch()
                    .intoGroups(OnboxBanners.ONBOX_BANNERS).values().stream()
                    .map(this::mapRecordToEntity)
                    .collect(Collectors.toList());
    }

    private OnboxBanner mapRecordToBanner(Result<Record> result) {
        var cities = result.into(Cities.CITIES.fields()).into(City.class);
        var screen = result.into(Screens.SCREENS.fields())
                .into(Screen.class).get(0);
        var banner = result.into(OnboxBanners.ONBOX_BANNERS.fields())
                .into(OnboxBanner.class).get(0);
        banner.setCities(cities);
        banner.setMobileNavigateTarget(screen);
        return banner;
    }

    private OnboxBanner mapRecordToEntity(Result<Record> result) {
        if (result.get(0) == null)
            return null;

        OnboxBanner onboxBanner = result.get(0)
                .into(OnboxBanners.ONBOX_BANNERS.fields())
                .into(OnboxBanner.class);

        List<City> cities = result
                .into(Cities.CITIES.fields())
                .into(City.class);

        onboxBanner.setId(result.get(0).get(OnboxBanners.ONBOX_BANNERS.ID));

        if (onboxBanner.getCitySelect().equals(CitySelectValues.Selection))
            onboxBanner.setCities(cities);

        if (onboxBanner.getCities() == null) {
            onboxBanner.setCities(new ArrayList<>());
        }

        Screen screen = result.get(0)
                .into(Screens.SCREENS.fields())
                .into(Screen.class);
        onboxBanner.setMobileNavigateTarget(screen);
        return onboxBanner;
    }

    protected Optional<OnboxBanner> insertCitiesListAndReturn(OnboxBannersRecord result, OnboxBanner data, boolean delete) {
        if (result == null)
            return Optional.empty();

        Long onboxId = result.get(OnboxBanners.ONBOX_BANNERS.ID);

        if (delete) {
            dslContext.delete(CityOnboxBanner.CITY_ONBOX_BANNER)
                    .where(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID.eq(onboxId))
                    .execute();
        }

        Long bannerId = result.get(OnboxBanners.ONBOX_BANNERS.ID);

        if (delete)
            dslContext.delete(CityOnboxBanner.CITY_ONBOX_BANNER).where(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID.eq(bannerId)).execute();

        if (data.getCitySelect() != null &&
                (data.getCitySelect().equals(CitySelectValues.Selection)
                        || data.getCitySelect().equals(CitySelectValues.All))) {
            var citiesList = data.getCities();
            if (citiesList != null) {
                citiesList.forEach(city -> {
                    city.setId(dslContext.select(Cities.CITIES.ID).from(Cities.CITIES)
                            .where(Cities.CITIES.NAME.eq(city.getName())).fetchOptionalInto(Long.class).get());
                });
                citiesList.forEach(cityId -> dslContext.insertInto(CityOnboxBanner.CITY_ONBOX_BANNER)
                        .set(CityOnboxBanner.CITY_ONBOX_BANNER.CITYID, cityId.getId())
                        .set(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOXBANNERID, bannerId)
                        .execute());
            }
        }

        return get(bannerId);
    }
}