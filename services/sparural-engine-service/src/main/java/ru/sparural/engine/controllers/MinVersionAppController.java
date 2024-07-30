package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.MinVersionAppDeviceTypeCreateDto;
import ru.sparural.engine.api.dto.MinVersionAppDeviceTypeDto;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeEntity;
import ru.sparural.engine.entity.MinVersionAppDeviceTypeFullEntity;
import ru.sparural.engine.services.MinVersionAppDeviceTypeService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class MinVersionAppController {
    private final MinVersionAppDeviceTypeService minVersionAppDeviceTypeService;

    @KafkaSparuralMapping("min-version/create")
    public Boolean create(@Payload MinVersionAppDeviceTypeCreateDto createDto) {
        var entity = new MinVersionAppDeviceTypeEntity();
        entity.setMinVersionApp(createDto.getVersionApp());
        entity.setDeviceTypeId(createDto.getDeviceTypeId());
        minVersionAppDeviceTypeService.create(entity);
        return true;
    }

    @KafkaSparuralMapping("min-version/device-type")
    public MinVersionAppDeviceTypeDto getByDeviceType(@RequestParam String deviceType) {
        var entity = minVersionAppDeviceTypeService.getByDeviceTypeName(deviceType);
        var dto = new MinVersionAppDeviceTypeDto();
        dto.setId(entity.getId());
        dto.setDeviceTypeName(entity.getDeviceType().getName());
        dto.setVersionApp(entity.getMinVersionApp());
        return dto;
    }

    @KafkaSparuralMapping("min-version/index")
    public List<MinVersionAppDeviceTypeDto> getAll() {
        List<MinVersionAppDeviceTypeFullEntity> entityList = minVersionAppDeviceTypeService.getAll();
        return entityList.stream().map(entity -> {
            var dto = new MinVersionAppDeviceTypeDto();
            dto.setId(entity.getId());
            dto.setDeviceTypeName(entity.getDeviceType().getName());
            dto.setVersionApp(entity.getMinVersionApp());
            return dto;
        }).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("min-version/update")
    public Boolean update(@Payload MinVersionAppDeviceTypeCreateDto createDto, @RequestParam Long id) {
        var entity = new MinVersionAppDeviceTypeEntity();
        entity.setMinVersionApp(createDto.getVersionApp());
        entity.setDeviceTypeId(createDto.getDeviceTypeId());
        minVersionAppDeviceTypeService.update(entity, id);
        return true;
    }

    @KafkaSparuralMapping("min-version/delete")
    public Boolean delete(@RequestParam Long id) {
        return minVersionAppDeviceTypeService.delete(id);
    }

}