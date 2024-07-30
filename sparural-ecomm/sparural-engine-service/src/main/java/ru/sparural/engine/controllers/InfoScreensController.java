package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.InfoScreensService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class InfoScreensController {

    private final InfoScreensService infoScreensService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("info-screens/create")
    public InfoScreenDto create(@Payload InfoScreenDto infoScreenDto) throws ValidationException, ResourceNotFoundException {
        return infoScreensService.create(infoScreenDto);
    }

    @KafkaSparuralMapping("info-screens/get")
    public InfoScreenDto get(@RequestParam Long id) throws ResourceNotFoundException {
        var dto = infoScreensService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.INFO_SCREEN_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("info-screens/list")
    public List<InfoScreenDto> list(@RequestParam Long city,
                                    @RequestParam Integer offset,
                                    @RequestParam Integer limit,
                                    @RequestParam Boolean showOnlyPublic,
                                    @RequestParam Long dateStart,
                                    @RequestParam Long dateEnd) {
        var values = infoScreensService.list(offset, limit, city, showOnlyPublic, dateStart, dateEnd);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.INFO_SCREEN_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });

        return values;
    }

    @KafkaSparuralMapping("info-screens/update")
    public InfoScreenDto update(@RequestParam Long id, @Payload InfoScreenDto infoScreenDto) throws ValidationException, ResourceNotFoundException {
        InfoScreenDto result = infoScreensService.update(id, infoScreenDto);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.INFO_SCREEN_PHOTO, result.getId());
        if (!files.isEmpty()) {
            result.setPhoto(files.get(files.size() - 1));
        }
        return result;
    }

    @KafkaSparuralMapping("info-screens/delete")
    public Boolean delete(@RequestParam Long id) {
        return infoScreensService.delete(id);
    }

}
