package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.services.PushActionsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class PushActionsController {

    private final PushActionsService pushActionsService;

    @KafkaSparuralMapping("push-actions/index")
    public List<ScreenDto> index(@RequestParam Integer offset,
                                 @RequestParam Integer limit) {
        return pushActionsService.list(offset, limit);
    }
}
