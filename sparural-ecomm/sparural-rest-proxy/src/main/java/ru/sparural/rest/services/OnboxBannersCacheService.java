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
import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.api.dto.OnboxBannerForUpdateDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.config.KafkaTopics;

@Service
@EnableCaching
@RequiredArgsConstructor
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class OnboxBannersCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Cacheable(cacheNames = "notificationsTypes", key = "#id")
    public DataResponse<OnboxBannerDto> get(Long id) {
        return new DataResponse<>(restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("onbox-banners/get")
                .withRequestParameter("id", id)
                .sendForEntity());
    }

    public DataResponse<OnboxBannerDto> create(OnboxBannerDto restRequest) {
        return new DataResponse<>(restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("onbox-banners/create")
                .withRequestBody(restRequest)
                .sendForEntity());
    }

    @CachePut(cacheNames = "onboxBanners", key = "#id")
    public DataResponse<OnboxBannerDto> update(OnboxBannerForUpdateDto restRequest, Long id) {
        return new DataResponse<>(restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("onbox-banners/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest)
                .sendForEntity());
    }

    @CacheEvict(cacheNames = "onboxBanners", key = "#id")
    public UnwrappedGenericDto<EmptyObject> delete(Long id) {
        Boolean succes = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("id", id)
                .withAction("onbox-banners/delete")
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder().success(succes).build();
    }

}
