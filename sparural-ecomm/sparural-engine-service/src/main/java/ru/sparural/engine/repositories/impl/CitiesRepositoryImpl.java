package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.repositories.CitiesRepository;
import ru.sparural.tables.Cities;
import ru.sparural.tables.CityOnboxBanner;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitiesRepositoryImpl implements CitiesRepository {

    private final DSLContext dslContext;

    @Override
    public List<City> fetch(int offset, int limit) {
        return dslContext
                .selectFrom(Cities.CITIES)
                .orderBy(Cities.CITIES.NAME.asc())
                .offset(offset)
                .limit(limit)
                .fetch().into(City.class);
    }

    @Override
    public Optional<City> findByName(String name) {
        return dslContext
                .selectFrom(Cities.CITIES)
                .where(Cities.CITIES.NAME.eq(name))
                .fetchOptional()
                .map(row -> row.into(City.class));
    }

    @Override
    public List<City> listCitiesByOnboxBannerId(Long id) {
        return dslContext.select()
                .from(Cities.CITIES)
                .leftJoin(CityOnboxBanner.CITY_ONBOX_BANNER)
                .on(Cities.CITIES.ID.eq(CityOnboxBanner.CITY_ONBOX_BANNER.CITY_ID))
                .where(CityOnboxBanner.CITY_ONBOX_BANNER.ONBOX_BANNER_ID.eq(id))
                .fetchInto(City.class);
    }

    @Override
    public Optional<String> getTimezoneById(Long id) {
        return dslContext.select(Cities.CITIES.TIMEZONE).from(Cities.CITIES)
                .where(Cities.CITIES.ID.eq(id))
                .fetchOptionalInto(String.class);
    }

}
