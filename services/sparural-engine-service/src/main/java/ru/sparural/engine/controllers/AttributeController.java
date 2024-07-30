package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.MerchantAttributeCreateOrUpdateDto;
import ru.sparural.engine.api.dto.merchant.Attribute;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.AttributeService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class AttributeController {
    private final AttributeService service;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("merchant-attributes/index")
    public List<Attribute> list(@RequestParam Integer offset,
                                @RequestParam Integer limit) {
        var values = service.list(offset, limit);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_ATTRIBUTE_ICON, dto.getId());
            if (!files.isEmpty()) {
                dto.setIcon(files.get(files.size() - 1));
            }
        });
        return values;
    }

    @KafkaSparuralMapping("merchant-attributes/get")
    public Attribute get(@RequestParam Long id) throws ResourceNotFoundException {
        var dto = service.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_ATTRIBUTE_ICON, dto.getId());
        if (!files.isEmpty()) {
            dto.setIcon(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("merchant-attributes/create")
    public Attribute create(@Payload MerchantAttributeCreateOrUpdateDto attribute) {
        return service.create(attribute);
    }

    @KafkaSparuralMapping("merchant-attributes/update")
    public Attribute update(@RequestParam Long id,
                            @Payload MerchantAttributeCreateOrUpdateDto attribute) {
        return service.update(id, attribute);
    }

    @KafkaSparuralMapping("merchant-attributes/delete")
    public Boolean delete(@RequestParam Long id) {
        return service.delete(id);
    }
}
