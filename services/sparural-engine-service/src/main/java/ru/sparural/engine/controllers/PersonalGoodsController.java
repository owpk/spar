package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.goods.GoodsDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsForSaveDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.GoodsService;
import ru.sparural.engine.services.PersonalGoodsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.RequestParam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                                       @RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        List<PersonalGoodsDto> result = new ArrayList<>();

        List<PersonalGoodsDto> goodsFromLoymax = loymaxService
                .getPersonalGoods(loymaxUser, "PersonalOffersGoods");
        if (goodsFromLoymax.isEmpty()) {
            return new ArrayList<>();
        }
        int i = 0;
        List<PersonalGoodsForSaveDto> toSave = new ArrayList<>();
        for (PersonalGoodsDto x : goodsFromLoymax) {
            GoodsDto goods = goodsService.getByExtGoodsId(x.getGoodsId());
            if (goods != null) {
                if (i >= offset) {

                    List<FileDto> photo = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PHOTO, goods.getId());
                    if (!photo.isEmpty()) {
                        goods.setPhoto(photo.get(photo.size() - 1));
                    }
                    List<FileDto> preview = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.GOODS_PREVIEW, goods.getId());
                    if (!preview.isEmpty()) {
                        goods.setPreview(preview.get(preview.size() - 1));
                    }
                    PersonalGoodsForSaveDto personalGoodsSave = new PersonalGoodsForSaveDto();
                    personalGoodsSave.setPriceDown(x.getPriceDown());
                    personalGoodsSave.setPriceUp(x.getPriceUp());
                    personalGoodsSave.setUserId(userId);
                    personalGoodsSave.setGoodsId(goods.getId());
                    personalGoodsSave.setBrandId(x.getBrandId());
                    personalGoodsSave.setAccepted(x.getAccepted());
                    personalGoodsSave.setCalculationMethod(x.getCalculationMethod());
                    personalGoodsSave.setPreferenceType(x.getPreferenceType());
                    personalGoodsSave.setStartDate(x.getStartDate());
                    personalGoodsSave.setEndDate(x.getEndDate());
                    personalGoodsSave.setPreferenceValue(x.getPreferenceValue());

                    toSave.add(personalGoodsSave);

                    x.setUserId(userId);
                    if (x.getStartDate() <= Instant.now().getEpochSecond()) {
                        if (x.getEndDate() == null || x.getEndDate() >= Instant.now().getEpochSecond()) {
                            result.add(x);
                            i++;
                        }
                    }
                    x.setId(goods.getId());
                    x.setGoods(goods);
                }
            }
            if (result.size() >= limit)
                break;
        }
        personalGoodsService.batchSave(toSave);
        return result;
    }

    @KafkaSparuralMapping("personal-goods/accept")
    public PersonalGoodsDto accept(@RequestParam Long goodsId,
                                   @RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);

        PersonalGoodsDto personalGood = personalGoodsService.getByUserIdGoodsId(userId, goodsId);
        GoodsForAdminDto goodsDto = goodsService.get(goodsId);

        GoodsDto goods = new GoodsDto();
        goods.setGoodsId(goodsDto.getGoodsId());
        goods.setId(goodsId);
        goods.setDescription(goodsDto.getDescription());
        goods.setPhoto(goods.getPhoto());
        goods.setPreview(goods.getPreview());
        goods.setName(goodsDto.getName());

        personalGood.setGoods(goods);

        loymaxService.acceptToGoods(loymaxUser, personalGood.getBrandId(), goodsDto.getGoodsId());

        personalGood.setAccepted(true);

        PersonalGoodsForSaveDto personalGoodsForSaveDto = new PersonalGoodsForSaveDto();
        personalGoodsForSaveDto.setUserId(userId);
        personalGoodsForSaveDto.setGoodsId(goods.getId());
        personalGoodsForSaveDto.setAccepted(personalGood.getAccepted());
        personalGoodsForSaveDto.setCalculationMethod(personalGood.getCalculationMethod());
        personalGoodsForSaveDto.setPreferenceType(personalGood.getPreferenceType());
        personalGoodsService.saveOrUpdate(personalGoodsForSaveDto);

        return personalGood;
    }
}