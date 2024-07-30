package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.engine.entity.Screen;
import ru.sparural.engine.services.ScreenService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class ScreensController {

    private final ScreenService screenService;

    @KafkaSparuralMapping("screens/index")
    public List<ScreenDto> index(@RequestParam Long offset,
                                 @RequestParam Long limit) {
        List<Screen> screens = screenService.fetch(offset, limit);
        return screens.stream()
                .map(screenService::createDtoFromEntity)
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("screens/get")
    public ScreenDto getById(@RequestParam Long id) {
        return screenService.createDtoFromEntity(screenService.findById(id));
    }

}