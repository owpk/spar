package ru.sparural.notification.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.notification.config.KafkaTopics;
import ru.sparural.notification.service.UserPushTokenService;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class UserPushTokenServiceImpl implements UserPushTokenService {
    private final SparuralKafkaRequestCreator sparuralKafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    @Override
    public void deleteByToken(String token) {
        sparuralKafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("push-tokens/remove")
                .withRequestParameter("token", token)
                .sendAsync();
    }
}
