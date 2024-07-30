package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.utils.Constants;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/merchant-formats", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "merchants")
public class MerchantFormatController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<Format>> list(@RequestParam(defaultValue = "0") Integer offset,
                                           @RequestParam(defaultValue = "30") Integer limit,
                                           @RequestParam(required = false) List<String> nameNotEqual) {
        List<Format> responses = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-formats/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("nameNotEqual", nameNotEqual)
                .sendForEntity();
        return DataResponse.<List<Format>>builder()
                .success(true)
                .data(responses)
                .version(Constants.VERSION)
                .build();
    }
}
