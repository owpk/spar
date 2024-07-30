package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.models.nominatim.NominatimReverseResponse;
import ru.sparural.engine.repositories.CitiesRepository;
import ru.sparural.engine.services.GeolocationService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.utils.rest.RestTemplate;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GeolocationServiceImpl implements GeolocationService {

    private final RestTemplate restTemplate;
    private final CitiesRepository citiesRepository;

    @Value("${sparural.nominatim.address}")
    private final String nominatimAddress;

    @Value("${sparural.nominatim.lang}")
    private final String nominatimLang;

    @Override
    public City findCityByCoordinates(CoordinatesDto coordinates) {
        String params = "?" + Map.of(
                "lat", String.valueOf(coordinates.getLatitude()),
                "lon", String.valueOf(coordinates.getLongitude()),
                "zoom", "10",
                "format", "jsonv2"
        ).entrySet().stream().map(entry -> {
            return entry.getKey() + "=" + entry.getValue();
        }).collect(Collectors.joining("&"));

        try {
            NominatimReverseResponse response = restTemplate.request()
                    .withHeaders(Map.of("accept-language", nominatimLang))
                    .setResponseType(NominatimReverseResponse.class)
                    .getForEntity(nominatimAddress + "/reverse" + params)
                    .orElseThrow(() -> new ResourceNotFoundException("Nominatim did not find city by coords"));

            if ((!"city".equals(response.getAddresstype())) && (!"town".equals(response.getAddresstype()))) {
                throw new ResourceNotFoundException("Nominatim did not find city by coords");
            }

            return citiesRepository
                    .findByName(response.getName()).orElseThrow(() -> new ResourceNotFoundException("Город не найден"));
        } catch (Exception e) {
            return citiesRepository.findByName("Челябинск")
                    .orElseThrow(() -> new ResourceNotFoundException("Город по умолчанию отсутствует в базе"));
        }
    }

}
