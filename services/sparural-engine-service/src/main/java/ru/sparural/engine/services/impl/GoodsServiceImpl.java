package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.goods.GoodsDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminCreateDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminDto;
import ru.sparural.engine.api.dto.goods.GoodsForAdminUpdateDto;
import ru.sparural.engine.entity.GoodsEntity;
import ru.sparural.engine.repositories.GoodsRepository;
import ru.sparural.engine.services.GoodsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {
    private final GoodsRepository goodsRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<GoodsForAdminDto> list(Integer offset, Integer limit, String search) {
        return createDtoList(goodsRepository.fetch(offset, limit, search));
    }

    @Override
    public GoodsForAdminDto get(Long id) {
        return createDto(goodsRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public GoodsForAdminDto get(String goodsId) {
        return createDto(goodsRepository.get(goodsId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public GoodsForAdminDto create(GoodsForAdminCreateDto goodsForAdminDto) {

        return createDto(goodsRepository.create(createEntity(goodsForAdminDto))
                .orElseThrow(() -> new ServiceException("Failed to create goods")));
    }

    @Override
    public GoodsForAdminDto update(Long id, GoodsForAdminUpdateDto goodsForAdminDto) {
        if (goodsRepository.findByGoodsId(id, goodsForAdminDto.getGoodsId()).isPresent()) {
            throw new ValidationException("Goods with this id is already exist");
        }

        var good = goodsRepository.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goods with this id more not exist"));

        if (good.getDraft() != null && !good.getDraft() && goodsForAdminDto.getDraft()) {
            throw new ValidationException("It is forbidden to change the value from false to true");
        }

        return createDto(goodsRepository.update(id, createEntity(goodsForAdminDto))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public GoodsForAdminDto update(String goodsId, GoodsForAdminUpdateDto goodsForAdminDto) {
        var good = goodsRepository.get(goodsId)
                .orElseThrow(() -> new ResourceNotFoundException("Goods with this id more not exist"));
        if (good.getDraft() != null && !good.getDraft() && goodsForAdminDto.getDraft()) {
            throw new ValidationException("It is forbidden to change the value from false to true");
        }
        return createDto(goodsRepository.update(goodsId, createEntity(goodsForAdminDto))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public Boolean delete(Long id) {
        goodsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return goodsRepository.delete(id);
    }

    @Override
    public Boolean delete(String id) {
        goodsRepository.get(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource not found"));
        return goodsRepository.delete(id);
    }

    @Override
    public GoodsForAdminDto createDto(GoodsEntity entity) {
        return dtoMapperUtils.convert(GoodsForAdminDto.class, () -> entity);
    }

    @Override
    public List<GoodsForAdminDto> createDtoList(List<GoodsEntity> entities) {
        return dtoMapperUtils.convertList(GoodsForAdminDto.class, () -> entities);
    }

    @Override
    public GoodsEntity createEntity(GoodsForAdminCreateDto dto) {
        return dtoMapperUtils.convert(GoodsEntity.class, () -> dto);
    }

    @Override
    public GoodsEntity createEntity(GoodsForAdminUpdateDto dto) {
        return dtoMapperUtils.convert(GoodsEntity.class, () -> dto);
    }

    @Override
    public GoodsDto getByExtGoodsId(String goodsId) {
        GoodsEntity goods = goodsRepository.get(goodsId)
                .orElse(null);
        if (goods == null) {
            return null;
        } else {
            return dtoMapperUtils.convert(goods, GoodsDto.class);
        }
    }

}
