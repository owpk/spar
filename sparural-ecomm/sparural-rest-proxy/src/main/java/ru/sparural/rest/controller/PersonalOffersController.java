package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.PersonalOfferCreateDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.PersonalOfferUpdateDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/personal-offers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("personal offers")
public class PersonalOffersController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @IsManagerOrAdmin
    @GetMapping
    public DataResponse<List<PersonalOfferDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                     @RequestParam(defaultValue = "30") Integer limit) {
        List<PersonalOfferDto> responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-offers/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)

                .sendForEntity();
        return DataResponse.<List<PersonalOfferDto>>builder()
                .data(responses)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    DataResponse<PersonalOfferDto> get(@PathVariable Long id) {
        PersonalOfferDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-offers/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<PersonalOfferDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<PersonalOfferDto> create(@Valid @Parameter @RequestBody DataRequest<PersonalOfferCreateDto> restRequest) {

        PersonalOfferDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-offers/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<PersonalOfferDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<PersonalOfferDto> update(
            @PathVariable Long id,
            @Valid @Parameter @RequestBody DataRequest<PersonalOfferUpdateDto> restRequest) {

        PersonalOfferDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("personal-offers/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<PersonalOfferDto>builder()
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
                .withAction("personal-offers/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();
    }

}
