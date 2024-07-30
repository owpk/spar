package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.MainBlockDto;
import ru.sparural.engine.services.MainBlockService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class MainBlocksController {

    private final MainBlockService mainBlockService;

    @KafkaSparuralMapping("main-blocks/list")
    public List<MainBlockDto> list(@RequestParam Integer offset, @RequestParam Integer limit) {
        return mainBlockService.list(offset, limit);
    }

    @KafkaSparuralMapping("main-blocks/update")
    public MainBlockDto update(@Payload MainBlockDto blockDto, @RequestParam String code) {
        return mainBlockService.update(code, blockDto);
    }
}
