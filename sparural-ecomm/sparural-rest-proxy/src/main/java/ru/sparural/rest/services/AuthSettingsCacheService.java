package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import ru.sparural.engine.api.dto.settings.AuthSettingDto;
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
public class AuthSettingsCacheService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<AuthSettingDto> get() {
        AuthSettingDto authSettingDto = restToKafkaService.sendForEntity(kafkaTopics.getEngineRequestTopicName(), "auth-settings/get");
        return new DataResponse<>(authSettingDto);
    }

    @PutMapping
    public DataResponse<AuthSettingDto> update(AuthSettingDto requestDto) {
        AuthSettingDto authSettingDto = restToKafkaService.sendForEntity(kafkaTopics.getEngineRequestTopicName(), "auth-settings/update", requestDto);
        return new DataResponse<>(authSettingDto);
    }

}
