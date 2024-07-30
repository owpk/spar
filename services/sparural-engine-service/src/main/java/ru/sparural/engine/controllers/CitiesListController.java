package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.CityDto;
import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.services.CitiesService;
import ru.sparural.engine.services.GeolocationService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class CitiesListController {

    private final GeolocationService geolocationService;
    private final CitiesService<City> citiesService;
    private final UserService userService;

    @KafkaSparuralMapping("cities/list")
    public List<CityDto> list(@RequestParam Integer offset,
                              @RequestParam Integer limit) {
        var citiesList = new ArrayList<CityDto>();
        citiesService.list(offset, limit)
                .forEach(x -> {
                    CityDto dto = new CityDto();
                    dto.setId(x.getId());
                    dto.setName(x.getName());
                    dto.setTimezone(x.getTimezone());
                    citiesList.add(dto);
                });
        return citiesList;
    }

    @KafkaSparuralMapping("cities/by-location")
    public CityDto getLocation(@Payload CoordinatesDto coordinates,
                               @RequestParam Long userId) throws ResourceNotFoundException {
        var city = geolocationService.findCityByCoordinates(coordinates);
        if (userId != 0) {
            userService.updateLastCityId(userId, city.getId());
        }
        var dto = new CityDto();
        dto.setId(city.getId());
        dto.setName(city.getName());
        dto.setTimezone(city.getTimezone());
        return dto;
    }
}
