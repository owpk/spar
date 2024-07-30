package ru.sparural.triggers.controller;

import lombok.RequiredArgsConstructor;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;
import ru.sparural.triggers.services.TriggersTypeService;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.triggers}")
@RequiredArgsConstructor
public class TriggersTypesController {

    private final TriggersTypeService triggersTypeService;

    @KafkaSparuralMapping("triggers-types/index")
    public List<TriggersTypeDTO> list(@RequestParam Integer offset,
                                      @RequestParam Integer limit) {
        return triggersTypeService.list(offset, limit);
    }
}