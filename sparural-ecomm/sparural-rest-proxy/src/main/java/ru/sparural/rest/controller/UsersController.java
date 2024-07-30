package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.UserSearchFilterDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UsersController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserDto> update(@Valid @Parameter @RequestBody DataRequest<UserDto> restRequest,
                                        @PathVariable Long id) {
        UserDto userDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(userDto);
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<UserDto> create(@Valid @Parameter @RequestBody DataRequest<UserDto> restRequest) {
        UserDto userDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return new DataResponse<>(userDto);
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response).build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserDto> get(@PathVariable Long id) {
        UserDto userDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return new DataResponse<>(userDto);
    }

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<UserDto>> list(UserSearchFilterDto search)
            throws ExecutionException, InterruptedException {
        search.setOffset(0);
        search.setLimit(30);
        search.setAlphabetSort("ASC");
        var totalUserCount = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/count")
                .withRequestBody(search)
                .sendAsync()
                .getFuture()
                .thenApply(future -> (Long) future.getPayload());
        List<UserDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/index")
                .withRequestBody(search)
                .sendForEntity();
        var result = new DataResponse<List<UserDto>>();
        result.setData(response);
        result.setMeta(Map.of("total_count", totalUserCount.get()));
        return result;
    }

}