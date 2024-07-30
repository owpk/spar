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
import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
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
public class UserRequestsSubjectsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public List<UserRequestsSubjectsDto> list(Integer offset, Integer limit) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests-subjects/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
    }

    public DataResponse<UserRequestsSubjectsDto> create(UserRequestsSubjectsDto userRequestsSubjectsDto) {
        UserRequestsSubjectsDto result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests-subjects/create")
                .withRequestBody(userRequestsSubjectsDto)
                .sendForEntity();
        return DataResponse.<UserRequestsSubjectsDto>builder()
                .success(true)
                .data(result)
                .version(1)
                .build();
    }

    @Cacheable(cacheNames = "userRequestsSubjects", key = "#id")
    public DataResponse<UserRequestsSubjectsDto> get(Long id) {
        UserRequestsSubjectsDto result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests-subjects/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<UserRequestsSubjectsDto>builder()
                .success(true)
                .data(result)
                .version(1)
                .build();
    }

    @CacheEvict(value = "userRequestsSubjects", key = "#id")
    public UnwrappedGenericDto<Void> delete(Long id) {
        Boolean result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests-subjects/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(result)
                .build();
    }

    @CachePut(value = "userRequestsSubjects", key = "#id")
    public DataResponse<UserRequestsSubjectsDto> update(Long id, UserRequestsSubjectsDto userRequestsSubjectsDto) {
        UserRequestsSubjectsDto result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user-requests-subjects/update")
                .withRequestParameter("id", id)
                .withRequestBody(userRequestsSubjectsDto)
                .sendForEntity();
        return DataResponse.<UserRequestsSubjectsDto>builder()
                .success(true)
                .data(result)
                .version(1)
                .build();
    }
}
