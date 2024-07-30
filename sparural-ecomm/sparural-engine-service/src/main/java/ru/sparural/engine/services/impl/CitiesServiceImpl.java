package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.repositories.CitiesRepository;
import ru.sparural.engine.services.CitiesService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class CitiesServiceImpl implements CitiesService<City> {

    private final CitiesRepository citiesRepository;

    @Override
    public List<City> list(int offset, int limit) {
        return citiesRepository.fetch(offset, limit);
    }

    @Override
    public City getByName(String name) {
        return citiesRepository.findByName(name).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public String getTimezoneById(Long id) {
        return citiesRepository.getTimezoneById(id).orElseThrow(ResourceNotFoundException::new);
    }
}
