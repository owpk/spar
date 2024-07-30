package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.engine.services.NotificationsTypesService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class NotificationsTypesController {

    private final NotificationsTypesService service;

    @KafkaSparuralMapping("notifications-types/index")
    public List<NotificationsTypesDto> list(@RequestParam Integer offset,
                                            @RequestParam Integer limit) {
        return service.list(offset, limit);
    }

    @KafkaSparuralMapping("notifications-types/get")
    public NotificationsTypesDto get(@RequestParam Long id) {
        return service.get(id);
    }

}
