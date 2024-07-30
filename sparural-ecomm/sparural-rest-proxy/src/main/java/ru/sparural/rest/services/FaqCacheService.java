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
import ru.sparural.engine.api.dto.FaqDTO;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class FaqCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public List<FaqDTO> list(Integer offset, Integer limit) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("faq/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
    }


    @Cacheable(value = "faq", key = "#id")
    public FaqDTO get(Long id) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("faq/get")
                .withRequestParameter("id", id)
                .sendForEntity();
    }


    @CachePut(value = "faq", key = "#id")
    public FaqDTO update(Long id, FaqDTO faqDTO) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("faq/update")
                .withRequestParameter("id", id)
                .withRequestBody(faqDTO)
                .sendForEntity();
    }

    @CacheEvict(value = "faq", key = "#id")
    public Boolean delete(Long id) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("faq/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
    }

    public FaqDTO create(FaqDTO faqDTO) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("faq/create")
                .withRequestBody(faqDTO)
                .sendForEntity();
    }
}