package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.engine.entity.City;

public interface GeolocationService {
    City findCityByCoordinates(CoordinatesDto coordinates);
}
