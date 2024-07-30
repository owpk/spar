package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.api.trigger.MessageTemplateRestDto;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.MessageTemplateService;
import ru.sparural.rest.utils.Constants;
import ru.sparural.triggerapi.dto.MessageTemplateRequestDto;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.triggerapi.dto.MessageTemplateDto;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${rest.base-url}/${rest.version}/messages-templates", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api("message template")
public class MessageTemplateController {
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final MessageTemplateService messageTemplateService;

    @GetMapping
//    @IsManagerOrAdmin
    @ResponseType(ControllerResponseType.WRAPPED)
    public List<MessageTemplateRestDto> list(@RequestParam(defaultValue = "0") Integer offset,
                                                           @RequestParam(defaultValue = "30") Integer limit,
                                                           @RequestParam String messageType) {
        return messageTemplateService.list(offset, limit, messageType);
    }

    @IsManagerOrAdmin
    @GetMapping("/{id}")
    @ResponseType(ControllerResponseType.WRAPPED)
    public MessageTemplateRestDto get(@PathVariable Long id) throws ExecutionException, InterruptedException {
        return messageTemplateService.get(id);
    }

    @IsManagerOrAdmin
    @PostMapping
    @ResponseType(ControllerResponseType.WRAPPED)
    public MessageTemplateRestDto create(@Valid @Parameter @RequestBody DataRequest<MessageTemplateRequestDto> restRequest) {
        return messageTemplateService.create(restRequest.getData());
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    @ResponseType(ControllerResponseType.WRAPPED)
    public MessageTemplateDto update(
            @PathVariable Long id,
            @Valid @Parameter @RequestBody DataRequest<MessageTemplateRequestDto> restRequest) {

        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("messages-templates/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getTriggerRequestTopicName())
                .withAction("messages-templates/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<Void>builder()
                .success(success)
                .build();
    }
}