package ru.sparural.engine.services.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.loymax.rest.dto.goods.LoymaxItemsDto;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.LoymaxTimeToSparTimeAdapter;
import ru.sparural.engine.repositories.PersonalGoodsRepository;
import ru.sparural.engine.services.GoodsService;
import ru.sparural.engine.services.PersonalGoodsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.engine.loymax.enums.PersonalGoodsName;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalGoodsServiceImpl implements PersonalGoodsService {

    private final DtoMapperUtils dtoMapperUtils;
    private final PersonalGoodsRepository personalGoodsRepository;
    private final LoymaxService loymaxService;
    private final GoodsService goodsService;

    @Override
    public PersonalGoodsEntity getByUserIdGoodsId(Long userId, Long goodsId) {
        return personalGoodsRepository.getByUserIdGoodId(userId, goodsId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

    }

    @Override
    public PersonalGoodsDto createDto(PersonalGoodsEntity entity) {
        return dtoMapperUtils.convert(entity, PersonalGoodsDto.class);
    }

    @Override
    public PersonalGoodsEntity createEntity(PersonalGoodsDto dto) {
        return dtoMapperUtils.convert(dto, PersonalGoodsEntity.class);
    }

    @Override
    public List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> entities) {
        return personalGoodsRepository.batchSaveOrUpdate(entities);
    }

    @Deprecated
    @Override
    public List<PersonalGoodsEntity> fetchByUserId(Integer offset, Integer limit, Long userId) {
        var entities = fetchFromLoymaxAndSave(userId);
        return entities.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<PersonalGoodsEntity> fetchByUserId(Integer offset, Integer limit, Long userId, PersonalGoodsName personalGoodsName) {
        var entities = fetchFromLoymaxAndSave(userId, personalGoodsName);
        return entities.stream().skip(offset).limit(limit).collect(Collectors.toList());
    }

    @Override
    public PersonalGoodsEntity saveOrUpdate(PersonalGoodsEntity goodEntity) {
        return personalGoodsRepository.saveOrUpdate(goodEntity).orElseThrow();
    }

    private List<PersonalGoodsEntity> fetchFromLoymaxAndSave(Long userId, PersonalGoodsName personalGoodsName) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        var goodsFromLoymax = loymaxService.getPersonalGoods(loymaxUser, personalGoodsName.getName());
        var extIds = goodsFromLoymax.getPreferences().stream().flatMap(x -> x.getItems().stream())
                .map(LoymaxItemsDto::getGoodsId).collect(Collectors.toList());
        List<GoodsEntity> goodsEntities = goodsService.fetchAllByLoymaxIds(extIds);

        Map<String, GoodsEntity> extIdGoodEntity = goodsEntities.stream()
                .collect(Collectors.toMap(GoodsEntity::getExtGoodsId, Function.identity()));
        Map<Long, GoodsEntity> idGoodsEntity = goodsEntities.stream()
                .collect(Collectors.toMap(GoodsEntity::getId, Function.identity()));

        List<PersonalGoodsEntity> convertedFromLoymax = goodsFromLoymax.getPreferences().stream()
                .flatMap(pref -> pref.getItems().stream().map(x -> new BrandIdItemEntry(pref.getBrandId(), x)))
                .map(entry -> mapFromLoymax(entry.item, userId, entry.brandId, extIdGoodEntity.get(entry.item.getGoodsId()),
                        goodsFromLoymax.getStartDate(), goodsFromLoymax.getEndDate()))
                .collect(Collectors.toList());
        var result = batchSaveOrUpdate(convertedFromLoymax);
        result.forEach(personalGood -> personalGood.setGoods(idGoodsEntity.get(personalGood.getGoodsId())));
        return result;
    }

    @Deprecated
    private List<PersonalGoodsEntity> fetchFromLoymaxAndSave(Long userId) {
        return this.fetchFromLoymaxAndSave(userId, PersonalGoodsName.PERSONAL_OFFERS_GOODS_PRICE);
    }

    @AllArgsConstructor
    private static class BrandIdItemEntry {
        String brandId;
        LoymaxItemsDto item;
    }

    private PersonalGoodsEntity mapFromLoymax(LoymaxItemsDto item, Long userId, String brandId, GoodsEntity goodsEntity, String startDate, String endDate) {
        var personalGood = new PersonalGoodsEntity();
        personalGood.setEndDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(endDate));
        personalGood.setStartDate(LoymaxTimeToSparTimeAdapter.convertToEpochSeconds(startDate));
        personalGood.setPriceUp(item.getPriceUp());
        personalGood.setPriceDown(item.getPriceDown());
        personalGood.setCalculationMethod(item.getCalculationMethod());
        personalGood.setAccepted(item.getAccepted());
        personalGood.setUserId(userId);
        personalGood.setPreferenceValue(Integer.valueOf(item.getPreferenceValue()));
        personalGood.setPreferenceType(item.getPreferenceType());
        personalGood.setBrandId(brandId);
        personalGood.setGoods(goodsEntity);
        personalGood.setGoodsId(goodsEntity != null ? goodsEntity.getId() : null);
        return personalGood;
    }

}
