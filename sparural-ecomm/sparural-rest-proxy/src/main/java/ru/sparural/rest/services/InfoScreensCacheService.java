package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class InfoScreensCacheService {
    private static final String CACHE_NAME = "infoScreens";
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final CacheManager cacheManager;

    @Cacheable(cacheNames = CACHE_NAME, key = "{#offset, #limit, #city, #showOnlyPublic, #dateStart, #dateEnd}")
    public DataResponse<List<InfoScreenDto>> list(Integer offset, Integer limit, Integer city,
                                                  Boolean showOnlyPublic, Long dateStart, Long dateEnd) {
        List<InfoScreenDto> list = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("info-screens/list")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("city", city)
                .withRequestParameter("showOnlyPublic", showOnlyPublic)
                .withRequestParameter("dateStart", dateStart)
                .withRequestParameter("dateEnd", dateEnd)
                .sendForEntity();
        return new DataResponse<>(list);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public DataResponse<InfoScreenDto> get(Long id) {
        InfoScreenDto infoScreenDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("info-screens/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return new DataResponse<>(infoScreenDto);
    }

    public DataResponse<InfoScreenDto> create(InfoScreenDto restRequest) {
        evictAllCacheValues(CACHE_NAME);
        InfoScreenDto infoScreenDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("info-screens/create")
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(infoScreenDto);
    }

    public DataResponse<InfoScreenDto> update(InfoScreenDto restRequest, Long id) {
        evictAllCacheValues(CACHE_NAME);
        InfoScreenDto infoScreenDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("info-screens/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(infoScreenDto);
    }

    public void delete(Long id) {
        evictAllCacheValues(CACHE_NAME);
        restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("info-screens/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
    }

    private void evictAllCacheValues(String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null)
            cache.clear();
    }

}
