package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.dto.DeliveryDTO;
import ru.sparural.engine.api.dto.DeliveryUpdateDto;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.DeliveryService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class DeliveryController {
    private final DeliveryService deliveryService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("delivery/v2/index")
    public List<DeliveryDTO> list(@RequestParam Integer offset,
                                  @RequestParam Integer limit,
                                  @RequestParam Boolean includeNotPublic) {
        var values = deliveryService.list(offset, limit, includeNotPublic);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.DELIVERY_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });


        return values;
    }

    @KafkaSparuralMapping("delivery/v1/index")
    public List<DeliveryDTO> list(@RequestParam Integer offset,
                                  @RequestParam Integer limit) {
        var values = deliveryService.list(offset, limit);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.DELIVERY_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });
        return values;
    }

    @KafkaSparuralMapping("delivery/get")
    public DeliveryDTO get(@RequestParam Long id) throws ResourceNotFoundException {
        var dto = deliveryService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.DELIVERY_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("delivery/delete")
    public Boolean delete(@RequestParam Long id) {
        return deliveryService.delete(id);
    }

    @KafkaSparuralMapping("delivery/update")
    public DeliveryDTO update(@RequestParam Long id,
                              @Payload DeliveryUpdateDto deliveryDTO) {
        return deliveryService.update(id, deliveryDTO);
    }

    @KafkaSparuralMapping("delivery/create")
    public DeliveryDTO create(@Payload DeliveryCreateDTO deliveryDTO) {
        return deliveryService.create(deliveryDTO);
    }
}
