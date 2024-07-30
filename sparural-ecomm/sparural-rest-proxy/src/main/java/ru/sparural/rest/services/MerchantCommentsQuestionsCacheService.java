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
import ru.sparural.engine.api.dto.MerchantCommentsQuestionDTO;
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
public class MerchantCommentsQuestionsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Cacheable(cacheNames = "merchantCommentsQuestions",key ="{#offset, #limit}")
    public DataResponse<List<MerchantCommentsQuestionDTO>> list(Integer offset, Integer limit) {
        List<MerchantCommentsQuestionDTO> merchantCommentsQuestionDTOS = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-questions/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<MerchantCommentsQuestionDTO>>builder()
                .success(true)
                .data(merchantCommentsQuestionDTOS)
                .version(Constants.VERSION)
                .build();
    }

    @CacheEvict(value = "merchantCommentsQuestions", key = "#code")
    public DataResponse<Boolean> delete(String code) {
        Boolean result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-questions/delete")
                .withRequestParameter("code", code)
                .sendForEntity();
        return DataResponse.<Boolean>builder()
                .success(result)
                .build();
    }

    @CachePut(value = "merchantCommentsQuestions", key = "#code")
    public DataResponse<MerchantCommentsQuestionDTO> update(String code, MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        MerchantCommentsQuestionDTO date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-questions/update")
                .withRequestBody(merchantCommentsQuestionDTO)
                .withRequestParameter("code", code)
                .sendForEntity();
        return DataResponse.<MerchantCommentsQuestionDTO>builder()
                .success(true)
                .data(date)
                .version(Constants.VERSION)
                .build();
    }

    public DataResponse<MerchantCommentsQuestionDTO> create(MerchantCommentsQuestionDTO merchantCommentsQuestionDTO) {
        MerchantCommentsQuestionDTO date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-questions/create")
                .withRequestBody(merchantCommentsQuestionDTO)
                .sendForEntity();
        return DataResponse.<MerchantCommentsQuestionDTO>builder()
                .success(true)
                .data(date)
                .version(Constants.VERSION)
                .build();
    }
}
