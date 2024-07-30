package ru.sparural.rest.services;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.PaymentSettingsDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.utils.Constants;
import ru.sparural.rest.config.KafkaTopics;


@Service
@RequiredArgsConstructor
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class PaymentSettingsCacheService {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    public PaymentSettingsDto get() {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("payment-settings/get")
                .sendForEntity();
    }

    public DataResponse<PaymentSettingsDto> update(PaymentSettingsDto paymentSettingsDto) {
        PaymentSettingsDto data = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("payment-setting/update")
                .withRequestBody(paymentSettingsDto)
                .sendForEntity();
        return DataResponse.<PaymentSettingsDto>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }
}
