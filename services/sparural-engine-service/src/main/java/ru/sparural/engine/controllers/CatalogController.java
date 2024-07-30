package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.CatalogDto;
import ru.sparural.engine.api.dto.CoordinatesDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.City;
import ru.sparural.engine.services.CatalogsService;
import ru.sparural.engine.services.CitiesService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.GeolocationService;
import ru.sparural.engine.services.UserService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogsService catalogsService;
    private final FileDocumentService fileDocumentService;
    private final GeolocationService geolocationService;
    private final UserService userService;
    private final CitiesService<City> citiesService;

    @KafkaSparuralMapping("catalogs/create")
    public CatalogDto create(@Payload CatalogDto catalogDto) {
        return catalogsService.createDtoFromEntity(
                catalogsService.create(catalogDto)
        );
    }

    @KafkaSparuralMapping("catalogs/get")
    public CatalogDto get(@RequestParam Long id) {
        var dto = catalogsService.createDtoFromEntity(
                catalogsService.get(id)
        );
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.CATALOG, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("catalogs/index")
    public List<CatalogDto> list(@RequestParam Long city,
                                 @RequestParam Integer offset,
                                 @RequestParam Integer limit) {
        var catalogs = catalogsService.list(offset, limit, city);
        var values = catalogs.stream()
                .map(catalogsService::createDtoFromEntity)
                .collect(Collectors.toList());
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.CATALOG, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });
        return values;
    }

    @KafkaSparuralMapping("catalogs/select-by-coordinates")
    public List<CatalogDto> selectByCoordinates(
            @RequestParam String userLongitude,
            @RequestParam String userLatitude,
            @RequestParam Long userId) {
        List<CatalogDto> catalogs;
        if (userLatitude != null && userLongitude != null) {
            var coordinatesDto = new CoordinatesDto();
            coordinatesDto.setLatitude(Double.parseDouble(userLatitude));
            coordinatesDto.setLongitude(Double.parseDouble(userLongitude));
            City city = geolocationService.findCityByCoordinates(coordinatesDto);

            if (city != null && userId != 0)
                userService.updateLastCityId(userId, city.getId());

            catalogs = list(city.getId(), 0, 30);
        } else {
            Long cityId;
            try {
                cityId = userService.getCityIdByUserId(userId);
            } catch (Exception e) {
                cityId = citiesService.getByName("Челябинск").getId();
            }
            catalogs = list(cityId, 0, 30);
        }
        return catalogs;
    }

    @KafkaSparuralMapping("catalogs/update")
    public CatalogDto update(@RequestParam Long id, @Payload CatalogDto catalogDto) {
        return catalogsService.createDtoFromEntity(
                catalogsService.update(id, catalogDto)
        );
    }

    @KafkaSparuralMapping("catalogs/delete")
    public Boolean delete(@RequestParam Long id) {
        return catalogsService.delete(id);
    }

}
