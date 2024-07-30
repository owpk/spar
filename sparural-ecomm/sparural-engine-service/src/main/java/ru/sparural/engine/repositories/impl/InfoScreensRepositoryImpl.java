package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.entity.InfoScreen;
import ru.sparural.engine.entity.enums.CitySelectValues;
import ru.sparural.engine.helpers.TimeHelper;
import ru.sparural.engine.repositories.InfoScreensRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.tables.Cities;
import ru.sparural.tables.CityInfoScreen;
import ru.sparural.tables.InfoScreens;
import ru.sparural.tables.records.InfoScreensRecord;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoScreensRepositoryImpl implements InfoScreensRepository {

    private final DSLContext dslContext;

    @Override
    @Transactional
    public InfoScreen create(InfoScreen data) throws ValidationException, ResourceNotFoundException {
        if (data.getCitySelect() == null)
            data.setCitySelect(CitySelectValues.Selection);

        var insert = dslContext.insertInto(InfoScreens.INFO_SCREENS)
                .set(InfoScreens.INFO_SCREENS.IS_PUBLIC, data.isPublic())
                .set(InfoScreens.INFO_SCREENS.DRAFT, data.getDraft())
                .set(InfoScreens.INFO_SCREENS.CREATED_AT, new Date().getTime())
                .set(InfoScreens.INFO_SCREENS.DATE_END, data.getDateEnd())
                .set(InfoScreens.INFO_SCREENS.DATE_START, data.getDateStart());
        if (data.getCitySelect() != null) {
            insert = insert.set(InfoScreens.INFO_SCREENS.CITY_SELECT,
                    CitySelectValues.getByVal(data.getCitySelect().getVal()).getVal());
        }

        var result = insert.returning().fetchOne();
        return insertCitiesListAndReturn(result, data, false);
    }

    @Override
    public boolean delete(Long id) {
        return dslContext.delete(InfoScreens.INFO_SCREENS).where(InfoScreens.INFO_SCREENS.ID.eq(id)).execute() == 1;
    }

    @Override
    @Transactional
    public InfoScreen update(Long id, InfoScreen data) {
        if (data.getCitySelect() == null)
            data.setCitySelect(CitySelectValues.Selection);

        var update = dslContext.update(InfoScreens.INFO_SCREENS)
                .set(InfoScreens.INFO_SCREENS.UPDATED_AT, TimeHelper.currentTime())
                .set(InfoScreens.INFO_SCREENS.IS_PUBLIC, data.isPublic());

        if (data.getDraft() != null)
            update.set(InfoScreens.INFO_SCREENS.DRAFT, data.getDraft());
        if (data.getDateEnd() != null)
            update.set(InfoScreens.INFO_SCREENS.DATE_END, data.getDateEnd());
        if (data.getDateStart() != null)
            update.set(InfoScreens.INFO_SCREENS.DATE_START, data.getDateStart());

        if (data.getCitySelect() != null)
            update = update.set(InfoScreens.INFO_SCREENS.CITY_SELECT,
                    CitySelectValues.getByVal(data.getCitySelect().getVal()).getVal());

        var result = update.where(InfoScreens.INFO_SCREENS.ID.eq(id)).returning().fetchOne();

        return insertCitiesListAndReturn(result, data, true);
    }

    protected InfoScreen insertCitiesListAndReturn(InfoScreensRecord result, InfoScreen data, boolean delete) {
        if (result == null)
            return null;

        Long infoId = result.get(InfoScreens.INFO_SCREENS.ID);

        if (delete)
            dslContext.delete(CityInfoScreen.CITY_INFO_SCREEN)
                    .where(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID.eq(infoId)).execute();

        if (data.getCitySelect().equals(CitySelectValues.Selection)) {
            var citiesList = data.getCities();
            if (citiesList != null)
                citiesList.forEach(cityId -> dslContext.insertInto(CityInfoScreen.CITY_INFO_SCREEN)
                        .set(CityInfoScreen.CITY_INFO_SCREEN.CITY_ID, cityId.getId())
                        .set(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID, infoId)
                        .execute());
        }

        return get(infoId);
    }

    @Override
    public InfoScreen get(Long id) {
        var res = dslContext
                .select()
                .from(InfoScreens.INFO_SCREENS)
                .leftJoin(CityInfoScreen.CITY_INFO_SCREEN)
                .on(InfoScreens.INFO_SCREENS.ID.eq(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID))
                .leftJoin(Cities.CITIES)
                .on(Cities.CITIES.ID.eq(CityInfoScreen.CITY_INFO_SCREEN.CITY_ID))
                .where(InfoScreens.INFO_SCREENS.ID.eq(id));
        var result = res.fetch();

        if (result.size() == 0) {
            throw new ResourceNotFoundException("Resource Not Found");
        }

        InfoScreen infoScreen = result.get(0).into(InfoScreen.class);
        List<City> cities = result.into(City.class);
        infoScreen.setId(result.get(0).get(InfoScreens.INFO_SCREENS.ID));

        if (infoScreen.getCitySelect().equals(CitySelectValues.Selection))
            infoScreen.setCities(cities);

        infoScreen.setDraft(null);
        if (infoScreen.getCities() == null) {
            infoScreen.setCities(new ArrayList<>());
        }
        return infoScreen;
    }

    protected List<InfoScreen> listResponse(Result<Record> result) {

        List<Long> infoScreenIDs = new ArrayList<>();
        var query = result.stream()
                .map(r -> {
                    InfoScreen infoScreen = r.into(InfoScreen.class);
                    infoScreen.setId(r.get(InfoScreens.INFO_SCREENS.ID));
                    if (CitySelectValues.Selection.equals(infoScreen.getCitySelect())) {
                        infoScreenIDs.add(infoScreen.getId());
                    }
                    infoScreen.setDraft(null);

                    if (infoScreen.getCities() == null) {
                        infoScreen.setCities(new ArrayList<>());
                    }

                    return infoScreen;
                })
                .collect(Collectors.toList());

        Map<Long, List<City>> citiesList = new HashMap<>();

        var res = dslContext
                .select()
                .from(CityInfoScreen.CITY_INFO_SCREEN)
                .leftJoin(Cities.CITIES)
                .on(CityInfoScreen.CITY_INFO_SCREEN.CITY_ID.eq(Cities.CITIES.ID))
                .where(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID.in(infoScreenIDs))
                .fetch()
                .stream()
                .map(r -> {
                    Long infoScreenId = r.get(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID);
                    citiesList.computeIfAbsent(infoScreenId, k -> new ArrayList<>());
                    citiesList.get(infoScreenId).add(r.into(City.class));
                    return null;
                })
                .collect(Collectors.toList());
        query.forEach(val -> val.setCities(citiesList.get(val.getId())));
        return query;
    }

    private Condition dateCondition(Long dateStart, Long dateEnd) {
        var condition = DSL.noCondition();
        var table = InfoScreens.INFO_SCREENS;
        if (dateStart != null)
            condition = condition.and(table.DATE_START.greaterOrEqual(dateStart));
        if (dateEnd != null)
            condition = condition.and(table.DATE_END.lessOrEqual(dateEnd));
        return condition;
    }

    private Condition currentDateCondition() {
        var currentTime = System.currentTimeMillis();
        var table = InfoScreens.INFO_SCREENS;
        return table.DATE_START.lessOrEqual(currentTime).and(table.DATE_END.greaterThan(currentTime));
    }

    @Override
    public List<InfoScreen> list(int offset, int limit, Boolean showOnlyPublic, Long dateStart, Long dateEnd) {
        Result<Record> query;
        if (showOnlyPublic) {
            query = dslContext
                    .select().from(InfoScreens.INFO_SCREENS)
                    .where(currentDateCondition()
                            .and(InfoScreens.INFO_SCREENS.IS_PUBLIC.eq(true))
                            .and(InfoScreens.INFO_SCREENS.DRAFT.eq(false)
                                    .or(InfoScreens.INFO_SCREENS.DRAFT.isNull())))
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        } else {
            query = dslContext
                    .select().from(InfoScreens.INFO_SCREENS)
                    .where(dateCondition(dateStart, dateEnd)
                            .and(InfoScreens.INFO_SCREENS.DRAFT.eq(false)
                                    .or(InfoScreens.INFO_SCREENS.DRAFT.isNull())))
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        }
        return listResponse(query);
    }

    @Override
    public List<InfoScreen> list(int offset, int limit, Long city, Boolean showOnlyPublic, Long dateStart, Long dateEnd) {
        Result<Record> query;

        if (showOnlyPublic) {
            query = dslContext
                    .select().from(InfoScreens.INFO_SCREENS)
                    .leftJoin(CityInfoScreen.CITY_INFO_SCREEN)
                    .on(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID.eq(InfoScreens.INFO_SCREENS.ID))
                    .where(currentDateCondition()
                            .and(InfoScreens.INFO_SCREENS.IS_PUBLIC.eq(true))
                            .and(InfoScreens.INFO_SCREENS.DRAFT.eq(false)
                                    .or(InfoScreens.INFO_SCREENS.DRAFT.isNull())))
                    .and(CityInfoScreen.CITY_INFO_SCREEN.CITY_ID.eq(city).or(InfoScreens.INFO_SCREENS.CITY_SELECT.eq(CitySelectValues.All.getVal())))
                    .orderBy(InfoScreens.INFO_SCREENS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        } else {
            query = dslContext
                    .select().from(InfoScreens.INFO_SCREENS)
                    .leftJoin(CityInfoScreen.CITY_INFO_SCREEN)
                    .on(CityInfoScreen.CITY_INFO_SCREEN.INFO_SCREEN_ID.eq(InfoScreens.INFO_SCREENS.ID))
                    .where(dateCondition(dateStart, dateEnd)
                            .and(InfoScreens.INFO_SCREENS.DRAFT.eq(false)
                                    .or(InfoScreens.INFO_SCREENS.DRAFT.isNull())))
                    .and(CityInfoScreen.CITY_INFO_SCREEN.CITY_ID.eq(city))
                    .or(InfoScreens.INFO_SCREENS.CITY_SELECT.eq(CitySelectValues.All.getVal()))
                    .orderBy(InfoScreens.INFO_SCREENS.ID.desc())
                    .offset(offset)
                    .limit(limit)
                    .fetch();
        }
        return listResponse(query);
    }
}
