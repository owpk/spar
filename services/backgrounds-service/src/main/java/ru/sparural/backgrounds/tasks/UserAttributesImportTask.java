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
public class UserAttributesImportTask implements BackgroundTask {

    private final EngineUserService engineUserService;
    private final SparuralKafkaTopics sparuralKafkaTopics;
    private final SparuralKafkaRequestCreator kafkaRequestCreator;

    @Override
    public void action() {
        var loymaxUsers = engineUserService.loadAllLoymaxUsers();

        var partitions = Iterables.partition(loymaxUsers.stream()
                    .map(LoymaxUserDto::getLoymaxUserId)
                .collect(Collectors.toList()), 500);

        partitions.forEach(partition -> {
            var loymaxUsersWrapper = new LoymaxUsersDto();
            loymaxUsersWrapper.setLoymaxUsersIds(partition);
            sendImportAttributesRequest(loymaxUsersWrapper);
        });
    }

    private void sendImportAttributesRequest(LoymaxUsersDto loymaxUsersWrapper) {
        kafkaRequestCreator.createRequestBuilder()
                .withTopicName(sparuralKafkaTopics.getEngineTopicName())
                .withAction("user-attributes/import")
                .withRequestBody(loymaxUsersWrapper)
                .sendAsync();
    }
}
