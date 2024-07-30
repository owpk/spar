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
import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.dto.DeliveryDTO;
import ru.sparural.engine.api.dto.DeliveryUpdateDto;
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
public class DeliveryCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    /**
     * filter params:
     * @param offset
     * @param limit
     * @param includeNotPublic - defines whether to show all data or only public ones
     * @return
     */
    public List<DeliveryDTO> listV2(Integer offset, Integer limit, boolean includeNotPublic) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/v2/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("includeNotPublic", includeNotPublic)
                .sendForEntity();
    }

    public List<DeliveryDTO> listV1(Integer offset, Integer limit) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/v1/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
    }

    @Cacheable(cacheNames = "delivery", key = "#id")
    public DeliveryDTO get(Long id) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/get")
                .withRequestParameter("id", id)
                .sendForEntity();
    }

    @CacheEvict(value = "delivery", key = "#id")
    public Boolean delete(Long id) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
    }

    @CachePut(value = "delivery", key = "#id")
    public DeliveryDTO update(Long id, DeliveryUpdateDto deliveryDTO) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/update")
                .withRequestParameter("id", id)
                .withRequestBody(deliveryDTO)
                .sendForEntity();
    }

    public DeliveryDTO create(DeliveryCreateDTO deliveryCreateDTO) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("delivery/create")
                .withRequestBody(deliveryCreateDTO)
                .sendForEntity();
    }
}
