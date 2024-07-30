package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.UserDeviceDto;
import ru.sparural.engine.entity.UserDevice;
import ru.sparural.engine.services.UsersDeviceTypeService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class UserDeviceInfoController {

    private final UsersDeviceTypeService usersDeviceTypeService;
    private final DtoMapperUtils mapperUtils;
    private final ObjectMapper objectMapper;

    @KafkaSparuralMapping("users-devices/save")
    public UserDeviceDto saveDeviceInfo(@RequestParam String deviceIdentifier, @Payload UserDeviceDto userDeviceDto, @RequestParam Long userId) throws JsonProcessingException {
        var entity = mapperUtils.convert(userDeviceDto, UserDevice.class);
        entity.setData(objectMapper.writeValueAsString(userDeviceDto.getData()));
        log.info("User device data is : " + entity.getData());
        var created = usersDeviceTypeService.save(deviceIdentifier, userId, entity);
        return mapperUtils.convert(created, UserDeviceDto.class);
    }

    @KafkaSparuralMapping("users-devices/get")
    public String getDeviceTypeById(@RequestParam Long deviceTypeId) {
        return usersDeviceTypeService.findByDeviceTypeId(deviceTypeId);
    }

}
