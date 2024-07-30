package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.recipes.CreateRecipeDto;
import ru.sparural.engine.api.dto.recipes.RecipeDto;
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
@RequestMapping(value = "${rest.base-url}/${rest.version}/recipes", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "recipes")
public class RecipesController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<RecipeDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                              @RequestParam(defaultValue = "30") Integer limit) {
        List<RecipeDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();



        return DataResponse.<List<RecipeDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<RecipeDto> get(@PathVariable Long id) {
        RecipeDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<RecipeDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<RecipeDto> create(@Valid @Parameter @RequestBody DataRequest<CreateRecipeDto> restRequest) {
        RecipeDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<RecipeDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<RecipeDto> update(@Valid @Parameter @RequestBody DataRequest<CreateRecipeDto> restRequest,
                                          @PathVariable Long id) {
        RecipeDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<RecipeDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping("/{id}/icon")
    @IsManagerOrAdmin
    public DataResponse<RecipeDto> icon(@Valid @Parameter @RequestBody DataRequest<RecipeDto> restRequest,
                                        @PathVariable Long id) {
        RecipeDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/icon")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<RecipeDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("recipes/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(response).build();
    }

}
