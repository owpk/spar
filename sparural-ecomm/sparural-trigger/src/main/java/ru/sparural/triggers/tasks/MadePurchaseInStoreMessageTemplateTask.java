package ru.sparural.triggers.tasks;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.check.CheckNotificationInfoDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.MessageTypeResolver;
import ru.sparural.triggers.annotations.SparuralUserTask;
import ru.sparural.triggers.config.KafkaTopics;
import ru.sparural.triggers.model.EventType;

/**
 * @author Vorobyev Vyacheslav
 */

@SparuralUserTask(EventType.MADE_PURCHASE_IN_STORE)
@RequiredArgsConstructor
public class MadePurchaseInStoreMessageTemplateTask implements UserTask {

    private final SparuralKafkaRequestCreator sparuralKafkaRequestCreator;
    private final KafkaTopics kafkaTopics;
    private final MessageTypeResolver messageTypeResolver;

    @Override
    public void executeUserTask(MessageTemplateDto messageTemplateDto, UserFilterDto filter) {
        List<CheckNotificationInfoDto> lastUserCheck = sparuralKafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("checks/lastNotification")
                .withRequestParameter("startTime",
                        messageTemplateDto.getTrigger().getDateStart())
                .withRequestBody(filter)
                .sendForEntity();

        if (lastUserCheck != null) {
            lastUserCheck.parallelStream().forEach(notification -> {
                messageTypeResolver.resolveAndSend(
                        messageTemplateDto,
                        notification.getUserNotificationInfo(),
                        notification.getCheck() != null ? notification.getCheck().getMerchantsId() : null
                );
            });

            Lists.partition(lastUserCheck, 1000)
                    .parallelStream()
                    .forEach(part -> {
                        var checkIds = new LongList();
                        checkIds.setList(part.stream()
                                .map(notification -> notification.getCheck().getId())
                                .collect(Collectors.toList())
                        );
                        sparuralKafkaRequestCreator.createRequestBuilder()
                                        .withTopicName(kafkaTopics.getEngineRequestTopicName())
                                        .withAction("checks/saveIsNotifCheck")
                                        .withRequestBody(checkIds)
                                        .sendAsync();
                    });
        }


    }
}
