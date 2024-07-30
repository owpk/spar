package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import ru.sparural.engine.api.dto.support.*;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/support-chats",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "support-chats")
public class SupportChatsController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<List<SupportChatsFullDto>> index(@ApiIgnore UserPrincipal userPrincipal,
                                                         @RequestParam(defaultValue = "0") Integer offset,
                                                         @RequestParam(defaultValue = "30") Integer limit) {
        List<SupportChatsFullDto> result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("senderId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<List<SupportChatsFullDto>>builder()
                .success(true)
                .data(result)
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<SupportChatsFullDto> get(@PathVariable Long id) {
        SupportChatsFullDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<SupportChatsFullDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @GetMapping("/{id}/messages")
    @IsManagerOrAdmin
    public DataResponse<List<SupportChatFullMessageDto>> indexMessages(@PathVariable Long id,
                                                                       @RequestParam(required = false) Long timestamp,
                                                                       @RequestParam(defaultValue = "30") Integer limit) {
        List<SupportChatFullMessageDto> result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/messages")
                .withRequestParameter("chatId", id)
                .withRequestParameter("timestamp", timestamp)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<SupportChatFullMessageDto>>builder()
                .success(true)
                .data(result)
                .version(Constants.VERSION)
                .build();
    }

    @PutMapping("/{id}/messages/{messageId}")
    @IsManagerOrAdmin
    public DataResponse<SupportChatFullMessageDto> editMessage(@PathVariable Long id,
                                                               @PathVariable Long messageId,
                                                               @RequestBody DataRequest<SupportChatCreateMessageDto> data) {
        SupportChatFullMessageDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/messages-edit")
                .withRequestParameter("chatId", id)
                .withRequestParameter("messageId", messageId)
                .withRequestBody(data.getData())
                .sendForEntity();
        return DataResponse.<SupportChatFullMessageDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping("/{id}/messages")
    @IsManagerOrAdmin
    public DataResponse<SupportChatFullMessageDto> createMessage(
            @ApiIgnore UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody DataRequest<SupportChatCreateMessageDto> data) {
        SupportChatFullMessageDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/messages-create")
                .withRequestParameter("chatId", id)
                .withRequestParameter("senderId", userPrincipal.getUserId())
                .withRequestBody(data.getData())
                .sendForEntity();
        return DataResponse.<SupportChatFullMessageDto>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping("/{id}/messages/read-messages")
    @IsManagerOrAdmin
    public DataResponse<List<SupportChatMessageDto>> setMessagesRead(
            @ApiIgnore UserPrincipal userPrincipal,
            @PathVariable Long id,
            @RequestBody SupportChatReadMessagesInfoDto data) {
        List<SupportChatMessageDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("support-chats/messages-read")
                .withRequestParameter("chatId", id)
                .withRequestParameter("senderId", userPrincipal.getUserId())
                .withRequestBody(data)
                .sendForEntity();
        return DataResponse.<List<SupportChatMessageDto>>builder()
                .data(response)
                .success(true)
                .build();
    }

}
