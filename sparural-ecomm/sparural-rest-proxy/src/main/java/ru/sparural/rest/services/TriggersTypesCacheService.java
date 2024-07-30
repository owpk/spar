package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class TriggersTypesCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Cacheable(value = "triggersTypes", key = "{#offset, #limit}")
    public DataResponse<List<TriggersTypeDTO>> list(Integer offset, Integer limit) {
        List<TriggersTypeDTO> triggersTypeDTOList = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("triggers-types/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<TriggersTypeDTO>>builder()
                .success(true)
                .data(triggersTypeDTOList)
                .version(1)
                .build();
    }
}
