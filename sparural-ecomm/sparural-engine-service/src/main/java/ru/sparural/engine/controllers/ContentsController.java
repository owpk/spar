package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.ContentDto;
import ru.sparural.engine.entity.Content;
import ru.sparural.engine.services.ContentsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class ContentsController {

    private final ContentsService<Content> contentsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("contents/create")
    public ContentDto create(@Payload ContentDto contentDto) {
        return mapperUtils.convert(contentDto, Content.class,
                contentsService::create, ContentDto.class);
    }

    @KafkaSparuralMapping("contents/list")
    public List<ContentDto> list(@RequestParam Integer offset, @RequestParam Integer limit) {
        return mapperUtils.convertList(ContentDto.class,
                () -> contentsService.list(offset, limit));
    }

    @KafkaSparuralMapping("contents/get")
    public ContentDto get(@RequestParam String alias) {
        return mapperUtils.convert(ContentDto.class, () -> contentsService.get(alias));
    }

    @KafkaSparuralMapping("contents/update")
    public ContentDto update(@RequestParam String alias, @Payload ContentDto contentDto) {
        return mapperUtils.convert(contentDto, Content.class,
                x -> contentsService.update(alias, x), ContentDto.class);
    }

    @KafkaSparuralMapping("contents/delete")
    public Boolean delete(@RequestParam String alias) {
        return contentsService.delete(alias);
    }
}
