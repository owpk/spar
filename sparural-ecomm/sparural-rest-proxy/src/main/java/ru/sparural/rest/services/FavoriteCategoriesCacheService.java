package ru.sparural.rest.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.CategoriesDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
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
public class FavoriteCategoriesCacheService {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final ObjectMapper objectMapper;
    private final KafkaTopics kafkaTopics;

    @Cacheable(cacheNames = "favCategories", key = "{#userId, #offset, #limit}")
    public DataResponse<List<CategoryDto>> list(List<String> roles,
                                                Long userId,
                                                Integer offset,
                                                Integer limit) throws JsonProcessingException {
        CategoriesDto dto = restToKafkaService
                .createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("favorite-categories/index")
                .withRequestParameter("roles", objectMapper.writeValueAsString(roles))
                .withRequestParameter("userId", userId)
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();

        return DataResponse.<List<CategoryDto>>builder()
                .success(true)
                .data(dto.getData())
                .meta(dto.getMeta())
                .version(Constants.VERSION)
                .build();
    }
}
