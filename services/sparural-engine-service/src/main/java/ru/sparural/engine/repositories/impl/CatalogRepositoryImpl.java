package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.Catalog;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.repositories.CatalogRepository;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.enums.CitySelectValues;
import ru.sparural.tables.CatalogCity;
import ru.sparural.tables.Catalogs;
import ru.sparural.tables.Cities;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.jsonArrayAgg;
import static org.jooq.impl.DSL.select;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogRepositoryImpl implements CatalogRepository {

    private final DSLContext dslContext;

    @Override
    public Optional<Catalog> create(Catalog data) {
        if (data.getCitySelect() == null)
            data.setCitySelect(CitySelectValues.Selection);
        var rec = dslContext.insertInto(Catalogs.CATALOGS)
                .set(Catalogs.CATALOGS.URL, data.getUrl())
                .set(Catalogs.CATALOGS.CITYSELECT, data.getCitySelect())
                .set(Catalogs.CATALOGS.DRAFT, data.isDraft())
                .set(Catalogs.CATALOGS.NAME, data.getName())
                .set(Catalogs.CATALOGS.CREATEDAT, new Date().getTime())
                .returning()
                .fetchOptionalInto(Catalog.class).orElseThrow(
                        () -> new ResourceNotFoundException("Cannot create catalog record"));
        var cities = data.getCities();
        if (cities != null) {
            cities.forEach(cityId -> {
                        if (cityId.getId() != null && rec.getId() != null)
                            dslContext.insertInto(CatalogCity.CATALOG_CITY)
                                    .set(CatalogCity.CATALOG_CITY.CITYID, cityId.getId())
                                    .set(CatalogCity.CATALOG_CITY.CATALOGID, rec.getId())
                                    .set(CatalogCity.CATALOG_CITY.CREATEDAT, TimeHelper.currentTime())
                                    .execute();
                    }
            );
        }
        return get(rec.getId());
    }

    @Override
    // TODO CASCADE
    public boolean delete(Long id) {
        boolean result = dslContext.deleteFrom(CatalogCity.CATALOG_CITY)
                .where(CatalogCity.CATALOG_CITY.CATALOGID.eq(id))
                .execute() > 0;
        return dslContext
                .delete(Catalogs.CATALOGS)
                .where(Catalogs.CATALOGS.ID.eq(id))
                .execute() == 1;
    }

    @Override
    public Optional<Catalog> update(Long id, Catalog data) {
        if (data.getCitySelect() == null)
            data.setCitySelect(CitySelectValues.Selection);
        dslContext.update(Catalogs.CATALOGS)
                .set(Catalogs.CATALOGS.CITYSELECT, data.getCitySelect())
                .set(Catalogs.CATALOGS.URL, data.getUrl())
                .set(Catalogs.CATALOGS.NAME, data.getName())
                .set(Catalogs.CATALOGS.DRAFT, data.isDraft())
                .set(Catalogs.CATALOGS.UPDATEDAT, new Date().getTime())
                .where(Catalogs.CATALOGS.ID.eq(id))
                .execute();
        var cities = data.getCities();
        int res = dslContext.delete(CatalogCity.CATALOG_CITY)
                .where(CatalogCity.CATALOG_CITY.CATALOGID.eq(id))
                .execute();
        log.trace("Updated catalog cities [deleted cities]: " + res);
        if (cities != null) {
            cities.forEach(cityId -> dslContext.insertInto(CatalogCity.CATALOG_CITY)
                    .set(CatalogCity.CATALOG_CITY.CITYID, cityId.getId())
                    .set(CatalogCity.CATALOG_CITY.CATALOGID, id)
                    .onConflict(CatalogCity.CATALOG_CITY.CITYID, CatalogCity.CATALOG_CITY.CATALOGID)
                    .doNothing()
                    .execute());
        }
        return get(id);
    }

    @Override
    public Optional<Catalog> get(Long id) {
        return basicSelect()
                .where(Catalogs.CATALOGS.ID.eq(id))
                .fetch().intoGroups(Catalogs.CATALOGS.fields())
                .values().stream()
                .map(this::mapRecordToCatalog)
                .findFirst();
    }

    private SelectOnConditionStep<Record> basicSelect() {
        return dslContext.select()
                .from(Catalogs.CATALOGS)
                .leftJoin(CatalogCity.CATALOG_CITY)
                .on(CatalogCity.CATALOG_CITY.CATALOGID.eq(Catalogs.CATALOGS.ID))
                .leftJoin(Cities.CITIES)
                .on(CatalogCity.CATALOG_CITY.CITYID.eq(Cities.CITIES.ID));
    }

    @Override
    public List<Catalog> fetch(int offset, int limit) {
        return dslContext.select(
                        Catalogs.CATALOGS.ID,
                        Catalogs.CATALOGS.NAME,
                        Catalogs.CATALOGS.URL,
                        Catalogs.CATALOGS.DRAFT,
                        Catalogs.CATALOGS.CITYSELECT,
                        field(
                                select(jsonArrayAgg(jsonObject(Cities.CITIES.ID,
                                        Cities.CITIES.NAME,
                                        Cities.CITIES.TIMEZONE)))
                                        .from(Cities.CITIES)
                                        .join(CatalogCity.CATALOG_CITY)
                                        .on(Cities.CITIES.ID.eq(CatalogCity.CATALOG_CITY.CITYID))
                                        .where(CatalogCity.CATALOG_CITY.CATALOGID.eq(Catalogs.CATALOGS.ID))
                        ).as("cities")
                )
                .from(Catalogs.CATALOGS)
                .where(Catalogs.CATALOGS.DRAFT.eq(false))
                .orderBy(Catalogs.CATALOGS.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetchInto(Catalog.class);
    }

    @Override
    public List<Catalog> findByCity(int offset, int limit, Long city) {
        var rec = dslContext.select()
                .from(Catalogs.CATALOGS)
                .leftJoin(CatalogCity.CATALOG_CITY)
                .on(CatalogCity.CATALOG_CITY.CATALOGID.eq(Catalogs.CATALOGS.ID))
                .leftJoin(Cities.CITIES)
                .on(CatalogCity.CATALOG_CITY.CITYID.eq(Cities.CITIES.ID))
                .where(Catalogs.CATALOGS.CITYSELECT.eq(CitySelectValues.All)
                        .or(CatalogCity.CATALOG_CITY.CITYID.eq(city)))
                .orderBy(Catalogs.CATALOGS.ID.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
        return rec
                .intoGroups(Catalogs.CATALOGS.fields())
                .values()
                .stream()
                .map(this::mapRecordToCatalog)
                .collect(Collectors.toList());
    }

    private Catalog mapRecordToCatalog(Result<Record> r) {
        var cat = r.into(Catalogs.CATALOGS.fields())
                .into(Catalog.class).get(0);
        var cities = r.into(Cities.CITIES.fields())
                .into(City.class);
        cat.setCities(cities);
        return cat;
    }

}