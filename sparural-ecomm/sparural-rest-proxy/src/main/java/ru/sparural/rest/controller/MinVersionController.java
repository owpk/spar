package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.MinVersionAppDeviceTypeCreateDto;
import ru.sparural.engine.api.dto.MinVersionAppDeviceTypeDto;
import ru.sparural.engine.api.dto.MinVersionAppDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/min-version", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "min-version")
public class MinVersionController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse create(@RequestBody MinVersionAppDeviceTypeCreateDto minVersionAppDto) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("min-version/create")
                .withRequestBody(minVersionAppDto)
                .sendForEntity();
        return DataResponse.builder()
                .success(success)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse update(@RequestBody MinVersionAppDeviceTypeCreateDto minVersionAppDto, @PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("min-version/update")
                .withRequestParameter("id", id)
                .withRequestBody(minVersionAppDto)
                .sendForEntity();
        return DataResponse.builder()
                .success(success)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("min-version/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.builder()
                .success(success)
                .build();
    }

    @GetMapping("/index")
    public DataResponse<List<MinVersionAppDeviceTypeDto>> getAll() {
        List<MinVersionAppDeviceTypeDto> dto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("min-version/index")
                .sendForEntity();
        return DataResponse.<List<MinVersionAppDeviceTypeDto>>builder()
                .success(true)
                .data(dto)
                .build();
    }

    @GetMapping
    @ResponseType(ControllerResponseType.WRAPPED)
    public MinVersionAppDeviceTypeDto getByDeviceTypeName(@RequestParam String deviceType) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("min-version/device-type")
                .withRequestParameter("deviceType", deviceType)
                .sendForEntity();
    }
}
