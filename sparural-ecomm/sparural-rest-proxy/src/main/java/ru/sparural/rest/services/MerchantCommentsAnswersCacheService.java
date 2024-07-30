package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.AnswerDTO;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.dto.DataResponse;

@Service
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@RequiredArgsConstructor
public class MerchantCommentsAnswersCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @CachePut(cacheNames = "merchantCommentsAnswers", key = "{#code, #answerId}")
    public DataResponse<AnswerDTO> update(String code, Long answerId, AnswerDTO date) {
        AnswerDTO answerDTO = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-answers/update")
                .withRequestParameter("code", code)
                .withRequestParameter("answerId", answerId)
                .withRequestBody(date)
                .sendForEntity();
        return DataResponse.<AnswerDTO>builder()
                .success(true)
                .data(answerDTO)
                .version(1)
                .build();

    }

    @CacheEvict(value = "merchantCommentsAnswers", key = "{#code, #answerId}")
    public DataResponse<Boolean> delete(String code, Long answerId) {
        Boolean result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-answers/delete")
                .withRequestParameter("code", code)
                .withRequestParameter("answerId", answerId)
                .sendForEntity();
        return DataResponse.<Boolean>builder()
                .success(result)
                .build();
    }

    public DataResponse<AnswerDTO> create(String code, AnswerDTO answerDTO) {
        AnswerDTO date = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("merchant-comments-answers/create")
                .withRequestParameter("code", code)
                .withRequestBody(answerDTO)
                .sendForEntity();
        return DataResponse.<AnswerDTO>builder()
                .success(true)
                .data(date)
                .version(1)
                .build();
    }
}
