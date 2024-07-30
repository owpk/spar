package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;

import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/currencies", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "currencies")
public class CurrenciesController {
    
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    @ResponseType(ControllerResponseType.WRAPPED)
    public List<Currency> index(@RequestParam(defaultValue = "0") Integer offset,
                                @RequestParam(defaultValue = "30") Integer limit) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("currencies/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
    }
}
