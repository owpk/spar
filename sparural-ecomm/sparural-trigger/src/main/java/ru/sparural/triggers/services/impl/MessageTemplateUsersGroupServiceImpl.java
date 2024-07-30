package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.triggerapi.dto.MessageTemplateUsersGroupDto;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import ru.sparural.triggers.config.KafkaTopics;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTemplateUsersGroupServiceImpl implements ru.sparural.triggers.services.MessageTemplateUsersGroupService {
    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final KafkaTopics kafkaTopics;

    @Override
    public List<MessageTemplateUsersGroupDto> findByMessageTemplateId(List<Long> userGroupIds) {
        try {
            return kafkaRequestCreator.createRequestBuilder()
                    .withTopicName(kafkaTopics.getEngineRequestTopicName())
                    .withAction("users-groups/index-by-group")
                    .withRequestParameter("groupIds", userGroupIds.stream().map(Object::toString)
                            .collect(Collectors.toList()))
                    .sendAsync()
                    .getFuture()
                    .thenApply(response -> {
                        List<UserGroupDto> groups = null;
                        try {
                            groups = (List<UserGroupDto>) response.getPayload();
                        } catch (Exception e) {
                            log.warn("group not found while selection");
                        }
                        return groups;
                    }).get().stream().map(g -> {
                        var dto = new MessageTemplateUsersGroupDto();
                        if (g != null) {
                            dto.setId(g.getId());
                            dto.setName(g.getName());
                        }
                        return dto;
                    }).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}