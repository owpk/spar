package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.NotificationsDto;
import ru.sparural.engine.api.dto.NotificationsListWithMetaDto;
import ru.sparural.engine.entity.NotificationsEntity;
import ru.sparural.engine.services.NotificationsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class NotificationsListController {

    private final NotificationsService notificationsService;
    private final ObjectMapper mapper;

    @KafkaSparuralMapping("notifications/index")
    public NotificationsListWithMetaDto list(@RequestParam Long userId,
                                             @RequestParam Integer offset,
                                             @RequestParam Integer limit,
                                             @RequestParam Boolean isReaded,
                                             @RequestParam List<String> type) throws JsonProcessingException {
        return notificationsService.list(userId, offset, limit, isReaded, type);
    }

    @KafkaSparuralMapping("notifications/read")
    public NotificationsDto update(@RequestParam Long id,
                                   @RequestParam Long userId) {
        return notificationsService.update(id, userId);
    }

    @KafkaSparuralMapping("notifications/save")
    public NotificationsDto save(@Payload NotificationsDto notificationsDto) {
        var entity = mapper.convertValue(notificationsDto, NotificationsEntity.class);
        var savedEntity = notificationsService.save(entity);
        return mapper.convertValue(savedEntity, NotificationsDto.class);
    }
}
