package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.UserDeviceDto;
import ru.sparural.engine.api.dto.support.*;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.SupportChatMessageEntity;
import ru.sparural.engine.entity.SupportChatsEntity;
import ru.sparural.engine.services.*;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class SupportChatsController {
    private final SupportChatsService supportChatsService;
    private final UserService userService;
    private final FileDocumentService fileDocumentService;
    private final CardsService cardsService;
    private final UsersDeviceTypeService deviceTypeService;

    @KafkaSparuralMapping("support-chats/index")
    public List<SupportChatsFullDto> list(@RequestParam Long senderId,
                                          @RequestParam Integer offset,
                                          @RequestParam Integer limit) {
        // Двойная сротировка: чаты сначала сортируются по наличию новых сообщений
        // (в начале выводятся чаты, в которых есть непрочитанные сообщения),
        // далее сортировка по дате отправки последнего сообщения, начиная от новых.
        return supportChatsService.index(offset, limit)
                .stream().collect(Collectors.groupingBy(x -> String.valueOf(x.getId()) + x.getMessage().getIsRead(),
                        Collectors.mapping(Function.identity(),
                                Collectors.collectingAndThen(
                                        Collectors.toList(), e -> e.stream()
                                                .sorted((f1, f2) ->
                                                        Long.compare(f2.getMessage().getCreatedAt(),
                                                                f1.getMessage().getCreatedAt()))
                                                .limit(1)
                                                .map(r -> mapEntityToDto(senderId, r))
                                                .collect(Collectors.toList())))))
                .values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }


    @KafkaSparuralMapping("support-chats/get")
    public SupportChatsFullDto get(@RequestParam Long id, @RequestParam Long senderId) {
        var result = supportChatsService.get(id);
        return mapEntityToDto(senderId, result);
    }

    @KafkaSparuralMapping("support-chats/messages")
    public List<SupportChatFullMessageDto> listMessages(
            @RequestParam Long chatId,
            @RequestParam Long timestamp,
            @RequestParam Integer limit) {
        return supportChatsService.indexMessages(chatId, timestamp, limit)
                .stream()
                .map(this::mapMessageEntityToFullDto)
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("support-chats/messages-edit")
    public SupportChatFullMessageDto editMessage(
            @RequestParam Long chatId,
            @RequestParam Long messageId,
            @Payload SupportChatCreateMessageDto data) {
        return mapMessageEntityToFullDto(
                supportChatsService.editMessage(chatId, messageId, data));
    }

    @KafkaSparuralMapping("support-chats/messages-create")
    public SupportChatFullMessageDto createMessage(
            @RequestParam Long senderId,
            @RequestParam Long chatId,
            @Payload SupportChatCreateMessageDto data) {
        return mapMessageEntityToFullDto(
                supportChatsService.createMessage(senderId, chatId, data));
    }

    @KafkaSparuralMapping("support-chats/messages-read")
    public List<SupportChatMessageDto> setMessagesRead(
            @RequestParam Long chatId,
            @Payload SupportChatReadMessagesInfoDto data) {
        return supportChatsService.setMessagesRead(chatId, data.getMessagesIds())
                .stream()
                .map(this::mapMessageEntityToDto)
                .collect(Collectors.toList());
    }

    private SupportChatsFullDto mapEntityToDto(Long senderId, SupportChatsEntity entity) {
        var unreadMessagesFuture = CompletableFuture
                .supplyAsync(() -> supportChatsService.countUnreadMessages(senderId, entity.getId()));
        var dto = new SupportChatsFullDto();
        var supportUserInfo = new SupportUserInfoDto();
        var userDto = userService.createDto(entity.getUser());

        List<FileDto> files = fileDocumentService.getFileByDocumentId(
                FileDocumentTypeField.USER_PHOTO, userDto.getId());
        if (!files.isEmpty())
            userDto.setPhoto(files.get(files.size() - 1));

        try {
            var deviceEntity = deviceTypeService.getByUserId(entity.getUser().getId());
            var deviceInfo = new UserDeviceDto();
            deviceInfo.setData(deviceEntity.getData());
            deviceInfo.setIdentifier(deviceEntity.getIdentifier());
            deviceInfo.setVersionApp(deviceEntity.getVersionApp());
            supportUserInfo.setDevice(deviceInfo);
        } catch (Exception e) {
            log.warn("no device found when creating support chat dto for user with id: " + entity.getUser().getId()
                    + ", set default 'null' value");
        }

        try {
            supportUserInfo.setCard(cardsService.selectAndBindUserCards(userDto.getId()));
        } catch (Exception e) {
            log.warn("exception binding cards when creating support chat dto for user with id: " + entity.getUser().getId()
                    + ", set default 'null' value");
        }

        supportUserInfo.setUser(userDto);
        dto.setUser(supportUserInfo);
        dto.setLastMessage(mapMessageEntityToFullDto(entity.getMessage()));
        dto.setId(entity.getId());
        try {
            dto.setUnreadMessagesCount(unreadMessagesFuture.get());
        } catch (InterruptedException | ExecutionException e) {
           log.warn("Cannot count unread messages for support chat: " + entity);
        }
        return dto;
    }

    private SupportChatFullMessageDto mapMessageEntityToFullDto(SupportChatMessageEntity entity) {
        var msgFullDto = new SupportChatFullMessageDto();
        var msgDto = mapMessageEntityToDto(entity);
        var userDto = userService.createDto(entity.getSender());

        List<FileDto> files = fileDocumentService.getFileByDocumentId(
                FileDocumentTypeField.USER_PHOTO, userDto.getId());
        if (!files.isEmpty())
            userDto.setPhoto(files.get(files.size() - 1));

        msgFullDto.setSender(userDto);
        msgFullDto.setUnwrappedFields(msgDto);
        return msgFullDto;
    }

    private SupportChatMessageDto mapMessageEntityToDto(SupportChatMessageEntity entity) {
        var msgDto = new SupportChatMessageDto();
        msgDto.setIsRead(false);
        msgDto.setMessageType(entity.getMessageType().getType());
        msgDto.setIsReceived(entity.getIsReceived());
        msgDto.setIsRead(entity.getIsRead());
        msgDto.setId(entity.getId());
        msgDto.setText(entity.getText());
        msgDto.setCreatedAt(entity.getCreatedAt());
        msgDto.setUpdatedAt(entity.getUpdatedAt());
        List<FileDto> msgFile = fileDocumentService.getFileByDocumentId(
                FileDocumentTypeField.SUPPORT_CHATS_MESSAGE_FILE, msgDto.getId());
        if (!msgFile.isEmpty())
            msgDto.setFile(msgFile.get(msgFile.size() - 1));
        return msgDto;
    }

}
