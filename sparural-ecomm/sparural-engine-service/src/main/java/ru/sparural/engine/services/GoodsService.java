package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.goods.GoodsDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminCreateDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminUpdateDto;
import ru.sparural.engine.entity.GoodsEntity;

import java.util.List;

public interface GoodsService {
    List<GoodsForAdminDto> list(Integer offset, Integer limit, String search);

    GoodsForAdminDto get(Long id);

    GoodsForAdminDto get(String goodsId);

    GoodsForAdminDto create(GoodsForAdminCreateDto goodsForAdminDto);

    GoodsForAdminDto update(Long id, GoodsForAdminUpdateDto goodsForAdminDto);

    GoodsForAdminDto update(String goodsId, GoodsForAdminUpdateDto goodsForAdminDto);

    Boolean delete(Long id);

    Boolean delete(String id);

    GoodsForAdminDto createDto(GoodsEntity entity);

    List<GoodsForAdminDto> createDtoList(List<GoodsEntity> entities);

    GoodsEntity createEntity(GoodsForAdminCreateDto dto);

    GoodsEntity createEntity(GoodsForAdminUpdateDto dto);

    GoodsDto getByExtGoodsId(String goodsId);

    List<GoodsEntity> fetchAllByLoymaxIds(List<String> extIds);
}
