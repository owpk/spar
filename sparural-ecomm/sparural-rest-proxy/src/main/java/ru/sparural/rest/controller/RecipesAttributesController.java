package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.recipes.RecipeAttributesDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/recipe-attributes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "recipes")
public class RecipesAttributesController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<RecipeAttributesDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                            @RequestParam(defaultValue = "30") Integer limit) {
        List<RecipeAttributesDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipe-attributes/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<RecipeAttributesDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<RecipeAttributesDto> get(@PathVariable Long id) {
        RecipeAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipe-attributes/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<RecipeAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<RecipeAttributesDto> create(@Valid @Parameter @RequestBody DataRequest<RecipeAttributesDto> restRequest) {
        RecipeAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipe-attributes/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<RecipeAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<RecipeAttributesDto> update(@Parameter @RequestBody DataRequest<RecipeAttributesDto> restRequest,
                                                  @PathVariable Long id) {
        RecipeAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipe-attributes/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<RecipeAttributesDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipe-attributes/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(response).build();
    }

}
