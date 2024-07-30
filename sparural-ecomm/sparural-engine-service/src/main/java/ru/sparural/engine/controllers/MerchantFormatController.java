package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.MerchantFormatService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import javax.annotation.Nullable;
import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class MerchantFormatController {
    private final MerchantFormatService service;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("merchant-formats/index")
    public List<Format> list(@RequestParam Integer offset,
                             @RequestParam Integer limit,
                             @Nullable @RequestParam List<String> nameNotEqual) {
        var values = service.list(offset, limit, nameNotEqual);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.MERCHANT_FORMAT_ICON, dto.getId());
            if (!files.isEmpty()) {
                dto.setIcon(files.get(files.size() - 1));
            }
        });
        return values;
    }
}
