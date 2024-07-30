package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggers.repositories.ScreenKafkaEngineRepository;

import java.util.concurrent.CompletableFuture;
import ru.sparural.triggers.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class ScreenKafkaEngineRepositoryImpl implements ScreenKafkaEngineRepository {
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    @Override
    public CompletableFuture<ScreenDto> getById(Long screenId) {
        return kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("screens/get")
                .withRequestParameter("id", screenId)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> (ScreenDto) resp.getPayload());
    }
}
