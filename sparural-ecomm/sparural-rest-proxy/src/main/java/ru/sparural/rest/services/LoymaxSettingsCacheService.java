package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import ru.sparural.engine.api.dto.settings.LoymaxSettingDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.config.KafkaTopics;

@Service
@RequiredArgsConstructor
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class LoymaxSettingsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public DataResponse<LoymaxSettingDto> get() {
        LoymaxSettingDto loymaxSettingDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("loymax-settings/get")
                .sendForEntity();
        return new DataResponse<>(loymaxSettingDto);
    }

    @PutMapping
    public DataResponse<LoymaxSettingDto> update(LoymaxSettingDto restRequest) {
        LoymaxSettingDto loymaxSettingDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("loymax-settings/update")
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(loymaxSettingDto);
    }

}
