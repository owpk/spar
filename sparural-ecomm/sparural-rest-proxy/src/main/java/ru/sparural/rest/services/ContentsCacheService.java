package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ContentDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@Service
@RequiredArgsConstructor
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class ContentsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public DataResponse<List<ContentDto>> list(Integer offset, Integer limit) {
        List<ContentDto> contentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("contents/list")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return new DataResponse<>(contentDto);
    }

    @Cacheable(cacheNames = "contents", key = "#alias")
    public DataResponse<ContentDto> get(String alias) {
        ContentDto contentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("contents/get")
                .withRequestParameter("alias", alias)
                .sendForEntity();
        return new DataResponse<>(contentDto);
    }

    public DataResponse<ContentDto> create(ContentDto restRequest) {
        ContentDto contentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("contents/create")
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(contentDto);
    }

    @CachePut(cacheNames = "contents", key = "#alias")
    public DataResponse<ContentDto> update(ContentDto restRequest,
                                           String alias) {
        ContentDto contentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("contents/update")
                .withRequestParameter("alias", alias)
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(contentDto);
    }

    @CacheEvict(cacheNames = "contents", key = "#alias")
    public UnwrappedGenericDto<EmptyObject> delete(String alias) {
        Boolean contentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("contents/delete")
                .withRequestParameter("alias", alias)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(contentDto)
                .build();
    }

}
