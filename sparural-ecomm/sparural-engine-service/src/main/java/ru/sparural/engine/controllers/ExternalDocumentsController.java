package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.ExternalDocumentDto;
import ru.sparural.engine.entity.ExternalDocument;
import ru.sparural.engine.services.ExternalDocumentsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class ExternalDocumentsController {

    private final ExternalDocumentsService<ExternalDocument> externalDocumentsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("external-documents/index")
    public List<ExternalDocumentDto> list(@RequestParam Integer offset, @RequestParam Integer limit) {
        return mapperUtils.convertList(ExternalDocumentDto.class,
                () -> externalDocumentsService.list(offset, limit));
    }

    @KafkaSparuralMapping("external-documents/get")
    public ExternalDocumentDto get(@RequestParam String alias) throws ResourceNotFoundException {
        return mapperUtils.convert(externalDocumentsService.get(alias), ExternalDocumentDto.class);
    }

    @KafkaSparuralMapping("external-documents/create")
    public ExternalDocumentDto create(@Payload ExternalDocumentDto externalDocumentDto) {
        return mapperUtils.convert(externalDocumentDto, ExternalDocument.class,
                externalDocumentsService::create, ExternalDocumentDto.class);
    }

    @KafkaSparuralMapping("external-documents/update")
    public ExternalDocumentDto update(@RequestParam String alias, @Payload ExternalDocumentDto externalDocumentDto) {
        return mapperUtils.convert(externalDocumentDto, ExternalDocument.class,
                x -> externalDocumentsService.update(alias, x), ExternalDocumentDto.class);
    }

    @KafkaSparuralMapping("external-documents/delete")
    public Boolean delete(@RequestParam String alias) {
        return externalDocumentsService.delete(alias);
    }
}
