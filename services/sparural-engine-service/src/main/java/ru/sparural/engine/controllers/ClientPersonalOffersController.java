package ru.sparural.engine.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.PersonalOfferDto;
import ru.sparural.engine.api.dto.PersonalOfferUserDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.LoymaxPersonalOfferService;
import ru.sparural.engine.services.PersonalOfferUserService;
import ru.sparural.engine.services.PersonalOffersService;
import ru.sparural.engine.utils.MagicInsert;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
@Slf4j
public class ClientPersonalOffersController {

    private final LoymaxService loymaxService;
    private final PersonalOffersService personalOffersService;
    private final PersonalOfferUserService personalOfferUserService;
    private final FileDocumentService fileDocumentService;
    private final LoymaxPersonalOfferService loymaxPersonalOfferService;

    @KafkaSparuralMapping("client-personal-offers/index")
    public List<PersonalOfferDto> get(@RequestParam Integer offset,
                                      @RequestParam Integer limit,
                                      @RequestParam Long userId) {

        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        //Get logicalNameList
        List<String> logicalNameList = loymaxService.getLogicalName(loymaxUser);
        ObjectNode objectNode;
        List<PersonalOfferDto> personalOfferDtoList = new ArrayList<>();

        for (String logicalName : logicalNameList) {
            PersonalOfferDto personalOfferDto = personalOffersService.getByAttribute(logicalName);
            if (personalOfferDto != null) {
                personalOfferDtoList.add(personalOfferDto);
            }
        }

        if (personalOfferDtoList.isEmpty()) return new ArrayList<>();

        for (PersonalOfferDto offer : personalOfferDtoList) {

            objectNode = loymaxService.getDateFromAttribute(loymaxUser, offer.getAttribute());

            if (objectNode != null) {
                if (personalOfferUserService.checkIfExist(userId, offer.getId())) {
                    PersonalOfferUserDto personalOfferUserDto = personalOfferUserService.getByUserIdAndOfferId(userId, offer.getId());
                    if (personalOfferUserDto != null) {
                        if (!objectNode.toString().equals(personalOfferUserDto.getData())) {
                            personalOfferUserDto.setData(objectNode.toString());
                            personalOfferUserService.updateData(personalOfferUserDto);
                        }
                    }
                } else {
                    PersonalOfferUserDto personalOfferUserDto = new PersonalOfferUserDto();
                    personalOfferUserDto.setUserId(userId);
                    personalOfferUserDto.setPersonalOfferId(offer.getId());
                    personalOfferUserDto.setData(objectNode.toString());
                    personalOfferUserService.create(personalOfferUserDto);
                }
            }
        }

        List<PersonalOfferUserDto> personalOfferUserList = personalOfferUserService.listByUserId(userId);
        List<PersonalOfferDto> result = new ArrayList<>();
        int i = 0;

        if (personalOfferUserList != null) {
            for (PersonalOfferUserDto x : personalOfferUserList) {
                if (result.size() >= limit) {
                    break;
                }
                PersonalOfferDto personalOfferDto = personalOffersService.get(x.getPersonalOfferId());
                if (personalOfferDto != null) {
                    try {

                        objectNode = (ObjectNode) new ObjectMapper().readTree(x.getData());
                        personalOfferDto.setDescription(MagicInsert.convert(objectNode, personalOfferDto.getDescription()));

                        long starDate;
                        long endDate;
                        if (objectNode.get("StartDate") != null) {
                            starDate = LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(String.valueOf(objectNode.get("StartDate")).replaceAll("\"", ""));
                            personalOfferDto.setBegin(starDate);
                        }
                        if (objectNode.get("EndDate") != null) {
                            endDate = LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(String.valueOf(objectNode.get("EndDate")).replaceAll("\"", ""));
                            personalOfferDto.setEnd(endDate);
                        }
                        log.info(String.valueOf(Instant.now().getEpochSecond()));
                        if (Instant.now().getEpochSecond() > personalOfferDto.getBegin()
                                && Instant.now().getEpochSecond() < personalOfferDto.getEnd()
                                && personalOfferDto.getIsPublic()
                                && i >= offset) {
                            result.add(personalOfferDto);
                            i++;
                        }
                    } catch (JsonProcessingException e) {
                        log.error("Json processing error while getting client personal offers", e);
                    }
                }
            }
        }

        result.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PHOTO, dto.getId());
            List<FileDto> previews = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.PERSONAL_OFFER_PREVIEW, dto.getId());
            if (!files.isEmpty())
                dto.setPhoto(files.get(files.size() - 1));
            if (!previews.isEmpty())
                dto.setPreview(previews.get(previews.size() - 1));
        });

        return result;
    }
}


