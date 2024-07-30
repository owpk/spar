package ru.sparural.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.CategoriesDto;
import ru.sparural.engine.api.dto.FavoriteCategoriesDataRequestDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;


@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/favorite-categories", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "category")
public class FavoriteCategoriesController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @GetMapping
    DataResponse<List<CategoryDto>> list(@ApiIgnore UserPrincipal userPrincipal,
                                         @RequestParam(defaultValue = "0") Integer offset,
                                         @RequestParam(defaultValue = "30") Integer limit) throws JsonProcessingException {

        Long userId = 0L;

        if (userPrincipal.getSecuredRoles().contains("ROLE_CLIENT")) {
            userId = userPrincipal.getUserId();
        }

        CategoriesDto dto = restToKafkaService
                .createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/index")
                .withRequestParameter("userId", userId)
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();


        if (dto == null || dto.getData() == null || dto.getData().size() == 0) {
            return DataResponse.<List<CategoryDto>>builder()
                    .data(dto.getData())
                    .version(Constants.VERSION)
                    .build();
        }

        return DataResponse.<List<CategoryDto>>builder()
                .success(true)
                .data(dto.getData())
                .meta(dto.getMeta())
                .version(Constants.VERSION)
                .build();
    }


    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @GetMapping("/{id}")
    DataResponse<CategoryDto> get(@PathVariable Long id,
                                  @ApiIgnore UserPrincipal userPrincipal) throws JsonProcessingException {

        Long userId = 0L;

        if (userPrincipal.getSecuredRoles().contains("ROLE_CLIENT")) {
            userId = userPrincipal.getUserId();
        }

        CategoryDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/get")
                .withRequestParameter("userId", userId)
                .withRequestParameter("id", id)
                .sendForEntity();

        return DataResponse.<CategoryDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    DataResponse<CategoryDto> update(@PathVariable Long id,
                                     @Valid @RequestBody DataRequest<FavoriteCategoriesDataRequestDto> restRequest) {
        //TODO: add/check validation
        //TODO: add photo
        CategoryDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<CategoryDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();
    }

    @IsClient
    @PostMapping("/select")
    public UnwrappedGenericDto<Void> select(@RequestParam List<Long> list,
                                            @ApiIgnore UserPrincipal userPrincipal) {
        restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/select")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestParameter("listToSelect", list)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(true)
                .build();
    }
}
