package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.request.UserRequestDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/user-requests", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "user-requests")
public class UserRequestsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping
    DataResponse<List<UserRequestDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                            @RequestParam(defaultValue = "30") Integer limit,
                                            @RequestParam(required = false) String search) {

        List<UserRequestDto> date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("search", "%" + search + "%")
                .sendForEntity();

        return DataResponse.<List<UserRequestDto>>builder()
                .success(true)
                .data(date)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    DataResponse<UserRequestDto> get(@PathVariable Long id) {

        UserRequestDto date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests/get")
                .withRequestParameter("id", id)
                .sendForEntity();

        return DataResponse.<UserRequestDto>builder()
                .success(true)
                .data(date)
                .version(Constants.VERSION)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("id", id)
                .withAction("user-requests/delete")
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(success).build();
    }
}
