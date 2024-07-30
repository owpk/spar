package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.ClientStatusDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.ClientStatusService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class ClientStatusController {
    private final ClientStatusService clientStatusService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("client-statues/index")
    public List<ClientStatusDto> list(@RequestParam Integer offset,
                                      @RequestParam Integer limit) {
        var values = clientStatusService.list(offset, limit);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.CLIENT_STATUS_ICON, dto.getId());
            if (!files.isEmpty()) {
                dto.setIcon(files.get(files.size() - 1));
            }
        });
        return values;
    }

    @KafkaSparuralMapping("client-statues/get")
    public ClientStatusDto get(@RequestParam Long id) throws ResourceNotFoundException {
        var dto = clientStatusService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.CLIENT_STATUS_ICON, dto.getId());
        if (!files.isEmpty()) {
            dto.setIcon(files.get(files.size() - 1));
        }
        return dto;
    }
}
