package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.City;

import java.util.List;
import java.util.Optional;

public interface CitiesRepository {

    List<City> fetch(int offset, int limit);

    Optional<City> findByName(String name);

    List<City> listCitiesByOnboxBannerId(Long id);

    Optional<String> getTimezoneById(Long id);
}
