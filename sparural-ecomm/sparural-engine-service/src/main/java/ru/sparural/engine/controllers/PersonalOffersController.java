package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.PersonalOfferCreateDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.PersonalOfferUpdateDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.PersonalOffersService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class PersonalOffersController {
    private final PersonalOffersService personalOffersService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("personal-offers/index")
    public List<PersonalOfferDto> list(@RequestParam Integer offset,
                                       @RequestParam Integer limit) {
        var values = personalOffersService.list(offset, limit);
        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PHOTO, dto.getId());
            if (!files.isEmpty()) {
                dto.setPhoto(files.get(files.size() - 1));
            }
        });

        values.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PREVIEW, dto.getId());
            if (!files.isEmpty()) {
                dto.setPreview(files.get(files.size() - 1));
            }
        });

        return values;
    }

    @KafkaSparuralMapping("personal-offers/get")
    public PersonalOfferDto get(@RequestParam Long id) {
        var dto = personalOffersService.get(id);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PHOTO, dto.getId());
        if (!files.isEmpty()) {
            dto.setPhoto(files.get(files.size() - 1));
        }
        List<FileDto> previews = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PREVIEW, dto.getId());
        if (!previews.isEmpty()) {
            dto.setPreview(previews.get(previews.size() - 1));
        }
        return dto;
    }

    @KafkaSparuralMapping("personal-offers/create")
    public PersonalOfferDto post(@Payload PersonalOfferCreateDto personalOfferDto) {
        return personalOffersService.create(personalOfferDto);
    }

    @KafkaSparuralMapping("personal-offers/update")
    public PersonalOfferDto update(@RequestParam Long id, @Payload PersonalOfferUpdateDto personalOfferDto) {
        return personalOffersService.update(id, personalOfferDto);
    }

    @KafkaSparuralMapping("personal-offers/delete")
    public Boolean delete(@RequestParam Long id) {
        return personalOffersService.delete(id);
    }
}
