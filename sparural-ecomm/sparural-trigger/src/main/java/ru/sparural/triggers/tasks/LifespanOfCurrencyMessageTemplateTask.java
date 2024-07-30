package ru.sparural.triggers.tasks;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.user.UserFilterDto;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.MessageTypeResolver;
import ru.sparural.triggers.annotations.SparuralUserTask;
import ru.sparural.triggers.model.EventType;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import ru.sparural.triggers.config.KafkaTopics;

@SparuralUserTask(EventType.LIFESPAN_OF_CURRENCY)
@RequiredArgsConstructor
public class LifespanOfCurrencyMessageTemplateTask implements UserTask {
    private final SparuralKafkaRequestCreator sparuralKafkaRequestCreator;
    private final KafkaTopics kafkaTopics;
    private final MessageTypeResolver messageTypeResolver;

    @Override
    public void executeUserTask(MessageTemplateDto messageTemplateDto, UserFilterDto filter) {
        var accTypeId = Optional.ofNullable(messageTemplateDto.getCurrencyId())
                .orElseThrow(() -> new RuntimeException("Currency id is null for " +
                        EventType.LIFESPAN_OF_CURRENCY.getEventTypeName() + " task"));
        var burningTime = Optional.ofNullable(messageTemplateDto.getCurrencyDaysBeforeBurning())
                .orElseThrow(() -> new RuntimeException("Burning time is null for " +
                        EventType.LIFESPAN_OF_CURRENCY.getEventTypeName() + " task"));

        List<UserNotificationInfoDto> users = sparuralKafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users/notifications-by-filter")
                .withRequestBody(filter)
                .sendForEntity();

        List<Long> accounts = sparuralKafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("accounts/lifespan-trigger-info")
                .withRequestParameter("currencyId", accTypeId)
                .withRequestParameter("burningTime", burningTime)
                .sendForEntity();

        var tempSet = new HashSet<>(accounts);
        var actualUsers = users.stream()
                .filter(x -> tempSet.contains(x.getUser().getId()))
                .collect(Collectors.toList());

        actualUsers.parallelStream().forEach(user ->
                messageTypeResolver.resolveAndSend(
                        messageTemplateDto, user, null));

        var triggerFiredFilter = new UserFilterDto();
        triggerFiredFilter.setUserIds(
                actualUsers.stream()
                        .map(x -> x.getUser().getId())
                        .collect(Collectors.toList()));

        sparuralKafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("accounts/set-trigger-fired")
                .withRequestBody(triggerFiredFilter)
                .sendAsync();
    }
}
