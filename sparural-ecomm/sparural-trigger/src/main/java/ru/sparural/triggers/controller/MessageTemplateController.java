package ru.sparural.triggers.controller;

import lombok.RequiredArgsConstructor;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggerapi.dto.MessageTemplateRequestDto;
import ru.sparural.triggers.services.MessageTemplateService;
import ru.sparural.triggers.utils.DtoMapperUtils;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.triggers}")
public class MessageTemplateController {
    private final MessageTemplateService messageTemplateService;

    @KafkaSparuralMapping("messages-templates/index")
    public List<MessageTemplateDto> list(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String messageType) {
        return messageTemplateService.list(offset, limit, messageType);
    }

    @KafkaSparuralMapping("messages-templates/get")
    public MessageTemplateDto get(@RequestParam Long id) {
        return messageTemplateService.get(id);
    }

    @KafkaSparuralMapping("messages-templates/create")
    public MessageTemplateDto create(@Payload MessageTemplateRequestDto messageTemplateDto) {
        return messageTemplateService.create(messageTemplateDto);
    }

    @KafkaSparuralMapping("messages-templates/update")
    public MessageTemplateDto update(@RequestParam Long id, @Payload MessageTemplateRequestDto messageTemplateDto) {
        return messageTemplateService.update(id, messageTemplateDto);
    }

    @KafkaSparuralMapping("messages-templates/delete")
    public Boolean delete(@RequestParam Long id) {
        return messageTemplateService.delete(id);
    }
}