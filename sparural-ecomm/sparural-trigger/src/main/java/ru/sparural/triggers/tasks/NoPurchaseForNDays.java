package ru.sparural.triggers.tasks;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.check.CheckNotificationInfoDto;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.MessageTypeResolver;
import ru.sparural.triggers.annotations.SparuralUserTask;
import ru.sparural.triggers.config.KafkaTopics;
import ru.sparural.triggers.model.EventType;
import ru.sparural.triggers.utils.TimeHelper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SparuralUserTask(EventType.NO_PURCHASE_FOR_N_DAYS)
@RequiredArgsConstructor
public class NoPurchaseForNDays implements UserTask {

    private final SparuralKafkaRequestCreator requestCreator;
    private final KafkaTopics kafkaTopics;
    private final MessageTypeResolver messageTypeResolver;

    @Override
    public void executeUserTask(MessageTemplateDto messageTemplateDto, UserFilterDto filter) {
        // By default, we should accept only registered users
        if (filter.getRegistrationType() == null)
            filter.setRegistrationType(UserFilterRegistrationTypes.REGISTRED);

        // collect all checks with null start time -> returns last check
        List<CheckNotificationInfoDto> lastUserCheck = requestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("checks/lastNotifications")
                .withRequestParameter("startTime", 0)
                .withRequestBody(filter)
                .sendForEntity();

        var targetStartTime = TimeHelper.minusDaysToEpoch(
                messageTemplateDto.getDaysWithoutPurchasing(), new Date());

        List<UserNotificationInfoDto> filtered = lastUserCheck.stream()
                .filter(notifyInfo -> notifyInfo.getCheck().getDateTime() < targetStartTime)
                .map(CheckNotificationInfoDto::getUserNotificationInfo)
                .collect(Collectors.toList());

        filtered.forEach(info ->
                messageTypeResolver.resolveAndSend(
                        messageTemplateDto, info, null));
    }
}
