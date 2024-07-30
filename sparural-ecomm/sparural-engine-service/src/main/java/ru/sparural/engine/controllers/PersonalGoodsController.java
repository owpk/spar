package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.goods.GoodsDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.GoodsService;
import ru.sparural.engine.services.PersonalGoodsService;
import ru.sparural.engine.loymax.enums.PersonalGoodsName;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class PersonalGoodsController {
    private final LoymaxService loymaxService;
    private final GoodsService goodsService;
    private final PersonalGoodsService personalGoodsService;
    private final FileDocumentService fileDocumentService;

    @KafkaSparuralMapping("personal-goods/index")
    public List<PersonalGoodsDto> list(@RequestParam Integer offset,
                                       @RequestParam Integer limit,
                                       @RequestParam Long userId,
                                       @RequestParam String loymaxName) {

        List<PersonalGoodsEntity> entities = personalGoodsService.fetchByUserId(offset, limit, userId, PersonalGoodsName.fromString(loymaxName));
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Deprecated
    public List<PersonalGoodsDto> list( Integer offset,
                                        Integer limit,
                                        Long userId) {
        List<PersonalGoodsEntity> entities = personalGoodsService.fetchByUserId(offset, limit, userId);
        return entities.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @KafkaSparuralMapping("personal-goods/accept")
    public PersonalGoodsDto accept(@RequestParam Long goodsId,
                                   @RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        var personalGood = personalGoodsService.getByUserIdGoodsId(userId, goodsId);
        GoodsForAdminDto goodsDto = goodsService.get(goodsId);
        var goodEntity = new GoodsEntity();
        goodEntity.setId(goodsDto.getId());
        goodEntity.setDraft(goodsDto.getDraft());
        goodEntity.setDescription(goodsDto.getDescription());
        goodEntity.setName(goodEntity.getName());
        goodEntity.setExtGoodsId(goodsDto.getGoodsId());

        personalGood.setGoodsId(goodsId);
        loymaxService.acceptToGoods(loymaxUser, personalGood.getBrandId(), goodsDto.getGoodsId());
        personalGood.setAccepted(true);

        var savedEntity = personalGoodsService.saveOrUpdate(personalGood);
        savedEntity.setGoods(goodEntity);
        return mapToDto(savedEntity);
    }

    private PersonalGoodsDto mapToDto(PersonalGoodsEntity entity) {
        var persGoodsDto = new PersonalGoodsDto();
        persGoodsDto.setAccepted(entity.getAccepted());
        persGoodsDto.setCalculationMethod(entity.getCalculationMethod());
        persGoodsDto.setEndDate(entity.getEndDate());
        persGoodsDto.setStartDate(entity.getStartDate());
        persGoodsDto.setId(entity.getId());
        persGoodsDto.setPreferenceType(entity.getPreferenceType());
        persGoodsDto.setPriceDown(entity.getPriceDown());
        persGoodsDto.setPreferenceValue(entity.getPreferenceValue());
        persGoodsDto.setPriceUp(entity.getPriceUp());
        if (entity.getGoods() != null) {
            var goods = entity.getGoods();
            var goodsDto = new GoodsDto();
            goodsDto.setGoodsId(goods.getExtGoodsId());
            goodsDto.setDescription(goods.getDescription());
            goodsDto.setId(goods.getId());
            goodsDto.setName(goods.getName());
            persGoodsDto.setGoods(goodsDto);

            List<FileDto> photo = fileDocumentService.getFileByDocumentId(
                    FileDocumentTypeField.GOODS_PHOTO, goods.getId());

            if (!photo.isEmpty())
                goodsDto.setPhoto(photo.get(photo.size() - 1));

            List<FileDto> preview = fileDocumentService.getFileByDocumentId(
                    FileDocumentTypeField.GOODS_PREVIEW, goods.getId());

            if (!preview.isEmpty())
                goodsDto.setPreview(preview.get(preview.size() - 1));
        }
        return persGoodsDto;
    }
}