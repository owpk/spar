package ru.sparural.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import ru.sparural.engine.api.dto.SiteKeyDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.config.KafkaTopics;

//@RestController
//@RequestMapping(value = "${rest.base-url}/${rest.version}/captcha-site-key", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
//@Api(tags = "captcha site key")
public class CaptchaController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    DataResponse<SiteKeyDto> getSiteKey() {
        SiteKeyDto siteKeyDto = new SiteKeyDto();
        siteKeyDto.setSiteKey(restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("captcha-site-key/get")
                .sendForEntity());
        return DataResponse.<SiteKeyDto>builder()
                .data(siteKeyDto)
                .build();
    }
}
