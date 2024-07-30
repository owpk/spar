package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplatesUserDto;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import ru.sparural.triggers.config.KafkaTopics;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTemplateUserServiceImpl implements ru.sparural.triggers.services.MessageTemplateUserService {
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    @Override
    public List<MessageTemplatesUserDto> findByMessageTemplateId(List<Long> userIds) {
        try {
            return kafkaRequestCreator.createRequestBuilder()
                    .withTopicName(kafkaTopics.getEngineRequestTopicName())
                    .withAction("users/by-groups-or-users-ids")
                    .withRequestParameter("rangeUserIds", userIds.stream().map(Object::toString).collect(Collectors.toList()))
                    .sendAsync()
                    .getFuture()
                    .thenApply(response -> {
                        List<UserDto> users = null;
                        try {
                            users = (List<UserDto>) response.getPayload();
                        } catch (Exception e) {
                            log.warn("user not found when while selection");
                        }
                        return users;
                    }).get().stream().map(u -> {
                        var dto = new MessageTemplatesUserDto();
                        if (u != null) {
                            dto.setId(u.getId());
                            dto.setFirstName(u.getFirstName());
                            dto.setLastName(u.getLastName());
                            dto.setEmail(u.getEmail());
                            dto.setPhoneNumber(u.getPhoneNumber());
                        }
                        return dto;
                    }).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}