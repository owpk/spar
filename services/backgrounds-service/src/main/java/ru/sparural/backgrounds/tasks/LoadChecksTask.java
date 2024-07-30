package ru.sparural.backgrounds.tasks;

import com.google.common.collect.Iterables;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sparural.backgrounds.SparuralKafkaTopics;
import ru.sparural.backgrounds.services.EngineUserService;
import ru.sparural.engine.api.dto.LoymaxUsersDto;
import ru.sparural.engine.api.dto.user.LoymaxUserDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class LoadChecksTask implements BackgroundTask {

    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final EngineUserService engineUserService;
    private final SparuralKafkaTopics kafkaTopics;

    @Override
    public void action() {
        var ids = engineUserService.loadAllLoymaxUsers().stream()
                .map(LoymaxUserDto::getLoymaxUserId)
                .collect(Collectors.toList());

        Iterables.partition(ids, 500)
                .forEach(part -> {
                    var wrapper = new LoymaxUsersDto();
                    wrapper.setLoymaxUsersIds(part);
                    sendLoadChecksRequest(wrapper);
                });
    }

    private void sendLoadChecksRequest(LoymaxUsersDto wrapper) {
        kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("checks/load")
                .withRequestBody(wrapper)
                .sendAsync();
    }
}
