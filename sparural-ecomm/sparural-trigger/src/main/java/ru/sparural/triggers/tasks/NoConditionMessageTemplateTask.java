package ru.sparural.triggers.tasks;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.MessageTypeResolver;
import ru.sparural.triggers.annotations.SparuralUserTask;
import ru.sparural.triggers.config.KafkaTopics;
import ru.sparural.triggers.model.EventType;

/**
 * @author Vorobyev Vyacheslav
 */
@SparuralUserTask(EventType.NO_CONDITION)
@RequiredArgsConstructor
public class NoConditionMessageTemplateTask implements UserTask {

    private final SparuralKafkaRequestCreator requestCreator;
    private final KafkaTopics kafkaTopics;
    private final MessageTypeResolver messageTypeResolver;

    @Override
    public void executeUserTask(MessageTemplateDto messagesTemplate, UserFilterDto filter) {
        List<UserNotificationInfoDto> users = requestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/notifications-by-filter")
                .withRequestBody(filter)
                .sendForEntity();

        users.parallelStream().forEach(user -> {
            messageTypeResolver.resolveAndSend(messagesTemplate, user, null);
        });
    }

}
