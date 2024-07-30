package ru.sparural.rest.services;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.api.ScreenRestDto;
import ru.sparural.rest.api.notifications.NotificationsTypesRestDto;
import ru.sparural.rest.api.trigger.MessageTemplateRestDto;
import ru.sparural.rest.api.trigger.MessageTemplateUsersGroupRestRestDto;
import ru.sparural.rest.api.trigger.MessageTemplatesUserRestDto;
import ru.sparural.rest.api.trigger.TriggerRestDto;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggerapi.dto.MessageTemplateRequestDto;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.UserGroupDto;
import ru.sparural.engine.api.dto.common.LongList;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.rest.api.file.FileRestDto;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTemplateService {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final ModelMapper mapper;

    public List<MessageTemplateRestDto> list(Integer offset, Integer limit, String messageType) {
        List<MessageTemplateDto> messageTemplatesList = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("messages-templates/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("messageType", messageType)
                .sendForEntity();

        return fillMessageTemplateRestDtoFromTrigger(messageTemplatesList);
    }

    private CompletableFuture<Map<Long, UserDto>> getUsersByIdsFuture(List<MessageTemplateDto> messageTemplatesList) {
        var userIds = messageTemplatesList.stream()
                .map(MessageTemplateDto::getUsers)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        var ids = new LongList();
        ids.setList(userIds);
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/listByIds")
                .withRequestBody(ids)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> ((List<UserDto>) resp.getPayload())
                        .stream()
                        .collect(Collectors.toMap(UserDto::getId, v -> v))
                );
    }

    private CompletableFuture<Map<Long, UserGroupDto>> getGroupsByIdsFuture(List<MessageTemplateDto> messageTemplatesList) {
        var groupIds = messageTemplatesList.stream()
                .map(MessageTemplateDto::getUsersGroup)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());

        var ids = new LongList();
        ids.setList(groupIds);
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("users-groups/listByIds")
                .withRequestBody(ids)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> ((List<UserGroupDto>) resp.getPayload())
                        .stream()
                        .collect(Collectors.toMap(UserGroupDto::getId, v -> v))
                );
    }

    private List<MessageTemplateRestDto> fillMessageTemplateRestDtoFromTrigger(List<MessageTemplateDto> messageTemplatesList) {
        var msgTmplIdScreenFutures = new HashMap<Long, CompletableFuture<ScreenDto>>();
        var msgTmplIdNotificationFutures = new HashMap<Long, CompletableFuture<NotificationsTypesDto>>();
        var msgTmplIdFileFutures = new HashMap<Long, CompletableFuture<List<FileDto>>>();

        var getUsersFuture = getUsersByIdsFuture(messageTemplatesList);
        var getGroupsFuture = getGroupsByIdsFuture(messageTemplatesList);

        messageTemplatesList.forEach(messageTemplate -> {
            var msgTmplId = messageTemplate.getId();

            if (messageTemplate.getScreenId() != null) {
                msgTmplIdScreenFutures.put(msgTmplId, getScreenFromEngine(messageTemplate.getScreenId()));
            }

            if (messageTemplate.getNotificationTypeId() != null) {
                msgTmplIdNotificationFutures.put(msgTmplId, getNotificationTypeFromEngine(messageTemplate.getNotificationTypeId()));
            }
            msgTmplIdFileFutures.put(msgTmplId, getFileFromEngine(msgTmplId));
        });

        List<CompletableFuture> futures = new LinkedList<>();
        futures.addAll(msgTmplIdScreenFutures.values());
        futures.addAll(msgTmplIdNotificationFutures.values());
        futures.addAll(msgTmplIdFileFutures.values());
        futures.add(getUsersFuture);
        futures.add(getGroupsFuture);
        CompletableFuture.allOf((CompletableFuture[]) futures.toArray(CompletableFuture[]::new)).join();

        var msgTmplIdScreenDtoMap = msgTmplIdScreenFutures.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, v -> {
                    try {
                        return v.getValue().get();
                    } catch (InterruptedException | ExecutionException ex) {
                        log.error("", ex);
                        throw new RuntimeException(ex);
                    }
                }));

        var msgTmplIdNotificationTypeMap = msgTmplIdNotificationFutures.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, v -> {
                    try {
                        return v.getValue().get();
                    } catch (InterruptedException | ExecutionException ex) {
                        log.error("", ex);
                        throw new RuntimeException(ex);
                    }
                }));

        var msgTmplIdFileMap = msgTmplIdFileFutures.entrySet()
                .stream()
                .collect(Collectors.toMap(Entry::getKey, v -> {
                    try {
                        return v.getValue().get();
                    } catch (InterruptedException | ExecutionException ex) {
                        log.error("", ex);
                        throw new RuntimeException(ex);
                    }
                }));

        try {
            Map<Long, UserGroupDto> groupsMap = getGroupsFuture.get();
            Map<Long, UserDto> usersMap = getUsersFuture.get();
            return messageTemplatesList.stream().map(messageTemplate -> convertToRestResponse(
                    messageTemplate,
                    msgTmplIdScreenDtoMap.get(messageTemplate.getId()),
                    msgTmplIdNotificationTypeMap.get(messageTemplate.getId()),
                    msgTmplIdFileMap.get(messageTemplate.getId()),
                    usersMap,
                    groupsMap
                )).collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException ex) {
               log.error("", ex);
            throw new RuntimeException(ex);
        }
    }

    public MessageTemplateRestDto get(Long id) {
        MessageTemplateDto messageTemplate = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("messages-templates/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return fillMessageTemplateRestDtoFromTrigger(Collections.singletonList(messageTemplate)).get(0);
    }

    private MessageTemplateRestDto convertToRestResponse(MessageTemplateDto messageTemplateDto,
                                                        ScreenDto screenDto,
                                                        NotificationsTypesDto notificationsTypesDto,
                                                        List<FileDto> photo,
                                                        Map<Long, UserDto> users,
                                                        Map<Long, UserGroupDto> groups) {
        var triggerDto = messageTemplateDto.getTrigger();
        var restTrigger = mapper.map(triggerDto, TriggerRestDto.class);
        var restResponse = new MessageTemplateRestDto();
        restResponse.setTrigger(restTrigger);
        restResponse.setId(messageTemplateDto.getId());
        restResponse.setMessage(messageTemplateDto.getMessage());
        restResponse.setName(messageTemplateDto.getName());
        restResponse.setLifetime(messageTemplateDto.getLifetime());
        restResponse.setMessageHTML(messageTemplateDto.getMessageHTML());
        restResponse.setMessageType(messageTemplateDto.getMessageType());
        restResponse.setRequred(messageTemplateDto.getRequred());
        restResponse.setCurrencyId(messageTemplateDto.getCurrencyId());
        restResponse.setCurrencyDaysBeforeBurning(messageTemplateDto.getCurrencyDaysBeforeBurning());
        restResponse.setDaysWithoutPurchasing(messageTemplateDto.getDaysWithoutPurchasing());

        if (screenDto!=null) {
            restResponse.setScreen(mapper.map(screenDto, ScreenRestDto.class));
        }

        if (notificationsTypesDto != null) {
            restResponse.setNotificationType(mapper.map(notificationsTypesDto, NotificationsTypesRestDto.class));
        }

        restResponse.setIsSystem(messageTemplateDto.getIsSystem());
        restResponse.setSendToEveryone(messageTemplateDto.getSendToEveryone());
        restResponse.setUsers(messageTemplateDto.getUsers()
                .stream()
                .filter(users::containsKey)
                .map(userId -> mapper.map(users.get(userId), MessageTemplatesUserRestDto.class))
                .collect(Collectors.toList())
        );

        restResponse.setUsersGroup(messageTemplateDto.getUsersGroup()
                .stream()
                .filter(groups::containsKey)
                .map(groupId -> mapper.map(groups.get(groupId), MessageTemplateUsersGroupRestRestDto.class))
                .collect(Collectors.toList())
        );

        var opt = Optional.ofNullable(photo)
                .map(x -> {
                            if (x.isEmpty()) return null;
                            var p = x.get(x.size() - 1);
                            return mapper.map(p, FileRestDto.class);
                        }
                )
                .orElse(null);
        restResponse.setPhoto(opt);
        return restResponse;
    }

    private CompletableFuture<ScreenDto> getScreenFromEngine(Long screenId) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("screens/get")
                .withRequestParameter("id", screenId)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> (ScreenDto) resp.getPayload());
    }

    private CompletableFuture<NotificationsTypesDto> getNotificationTypeFromEngine(Long notifTypeId) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("notifications-types/get")
                .withRequestParameter("id", notifTypeId)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> (NotificationsTypesDto) resp.getPayload());
    }

    private CompletableFuture<List<FileDto>> getFileFromEngine(Long msgTmplId) {
        var fileType = FileDocumentTypeField.MESSAGE_TEMPLATE_PHOTO.toString();
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("file/get")
                .withRequestParameter("typeField", fileType)
                .withRequestParameter("id", msgTmplId)
                .sendAsync()
                .getFuture()
                .thenApply(resp -> (List<FileDto>) resp.getPayload());
    }

    public MessageTemplateRestDto create(MessageTemplateRequestDto data) {
        MessageTemplateDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("messages-templates/create")
                .withRequestBody(data)
                .sendForEntity();
        return fillMessageTemplateRestDtoFromTrigger(Collections.singletonList(response)).get(0);
    }
}
