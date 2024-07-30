package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.user.UserAttributesDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/users_attributes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UserAttributesController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<UserAttributesDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                      @RequestParam(defaultValue = "30") Integer limit) {
        List<UserAttributesDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-attributes/index")
                .withRequestParameters(
                        Map.of(
                                "offset", offset,
                                "limit", limit))
                .sendForEntity();
        return DataResponse.<List<UserAttributesDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserAttributesDto> get(@PathVariable Long id) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-attributes/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<UserAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<UserAttributesDto> create(@Valid @Parameter @RequestBody DataRequest<UserAttributesDto> restRequest) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-attributes/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<UserAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserAttributesDto> update(@Valid @Parameter @RequestBody DataRequest<UserAttributesDto> restRequest,
                                               @PathVariable Long id) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-attributes/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<UserAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-attributes/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(response).build();
    }

}
