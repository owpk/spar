package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.engine.api.dto.UsersGroupUserDto;
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

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/users-groups", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UserGroupController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<UserGroupDto>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit,
            @RequestParam(defaultValue = "") String search) {
        List<UserGroupDto> list = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/index")
                .withRequestParameters(Map.of("offset", offset, "limit", limit, "search", search))
                .sendForEntity();
        return new DataResponse<>(list);
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserGroupDto> get(@PathVariable Long id) {
        UserGroupDto userGroupDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return new DataResponse<>(userGroupDto);
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<UserGroupDto> create(@Valid @RequestBody DataRequest<UserGroupDto> body) {
        UserGroupDto userGroupDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/create")
                .withRequestBody(body.getData())
                .sendForEntity();
        return new DataResponse<>(userGroupDto);
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<UserGroupDto> update(
            @RequestBody DataRequest<UserGroupDto> body,
            @PathVariable Long id) {
        UserGroupDto userGroupDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/update")
                .withRequestParameter("id", id)
                .withRequestBody(body.getData())
                .sendForEntity();
        return new DataResponse<>(userGroupDto);
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(success).build();
    }

    @PostMapping("/{id}/add-users")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> addUsers(@PathVariable Long id,
                                                     @RequestBody UnwrappedGenericDto<UsersGroupUserDto> body) {
        Boolean userGroupDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/add-users")
                .withRequestParameter("id", id)
                .withRequestBody(body.getData())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(userGroupDto).build();
    }

    @PostMapping("/{id}/delete-users")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> removeUsers(@PathVariable Long id,
                                                        @RequestBody UnwrappedGenericDto<UsersGroupUserDto> body) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/delete-users")
                .withRequestParameter("id", id)
                .withRequestBody(body.getData())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(success).build();
    }

}
