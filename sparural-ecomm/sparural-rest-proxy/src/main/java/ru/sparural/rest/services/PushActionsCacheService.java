package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.utils.Constants;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;


@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class PushActionsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Cacheable(value = "pushActions", key = "{#offset, #limit}")
    public DataResponse<List<ScreenDto>> index(Integer offset, Integer limit) {
        List<ScreenDto> date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("push-actions/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<ScreenDto>>builder()
                .success(true)
                .data(date)
                .version(Constants.VERSION)
                .build();
    }

}
