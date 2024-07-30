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
import ru.sparural.engine.api.dto.ExternalDocumentDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;

import java.util.List;
import java.util.Map;
import ru.sparural.rest.config.KafkaTopics;

@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class ExternalDocumentsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public DataResponse<List<ExternalDocumentDto>> list(Integer offset, Integer limit) {
        List<ExternalDocumentDto> list = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("external-documents/index")
                .withRequestParameters(Map.of("offset", offset, "limit", limit))
                .sendForEntity();
        return new DataResponse<>(list);
    }

    @Cacheable(cacheNames = "extDocs", key = "#alias")
    public DataResponse<ExternalDocumentDto> get(String alias) {
        ExternalDocumentDto externalDocumentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("external-documents/get")
                .withRequestParameter("alias", alias)
                .sendForEntity();
        return new DataResponse<>(externalDocumentDto);
    }

    public DataResponse<ExternalDocumentDto> create(ExternalDocumentDto restRequest) {
        ExternalDocumentDto externalDocumentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("external-documents/create")
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(externalDocumentDto);
    }

    @CachePut(cacheNames = "extDocs", key = "#alias")
    public DataResponse<ExternalDocumentDto> update(ExternalDocumentDto restRequest, String alias) {
        ExternalDocumentDto externalDocumentDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("external-documents/update")
                .withRequestParameter("alias", alias)
                .withRequestBody(restRequest)
                .sendForEntity();
        return new DataResponse<>(externalDocumentDto);
    }

    @CacheEvict(cacheNames = "extDocs", key = "#alias")
    public UnwrappedGenericDto<Void> delete(String alias) {
        Boolean data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("external-documents/delete")
                .withRequestParameter("alias", alias)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder().success(data).build();
    }
}