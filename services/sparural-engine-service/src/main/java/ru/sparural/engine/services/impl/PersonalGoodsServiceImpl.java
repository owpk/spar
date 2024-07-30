package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.goods.PersonalGoodsDto;
import ru.sparural.engine.api.dto.goods.PersonalGoodsForSaveDto;
import ru.sparural.engine.entity.PersonalGoodsEntity;
import ru.sparural.engine.repositories.PersonalGoodsRepository;
import ru.sparural.engine.services.PersonalGoodsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalGoodsServiceImpl implements PersonalGoodsService {

    private final DtoMapperUtils dtoMapperUtils;
    private final PersonalGoodsRepository personalGoodsRepository;

    @Override
    public PersonalGoodsDto getByUserIdGoodsId(Long userId, Long goodsId) {
        return createDto(personalGoodsRepository.getByUserIdGoodId(userId, goodsId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found")));

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
    public PersonalGoodsDto saveOrUpdate(PersonalGoodsForSaveDto dto) {
        PersonalGoodsEntity entity = personalGoodsRepository
                .saveOrUpdate(dtoMapperUtils.convert(dto, PersonalGoodsEntity.class))
                .orElse(null);
        if (entity == null) {
            return null;
        } else {
            return createDto(entity);
        }
    }

    @Override
    public List<PersonalGoodsEntity> batchSave(List<PersonalGoodsForSaveDto> dto) {
        return personalGoodsRepository
                .batchSaveOrUpdate(dtoMapperUtils.convertList(PersonalGoodsEntity.class, dto));
    }

    @Override
    public List<PersonalGoodsEntity> batchSaveOrUpdate(List<PersonalGoodsEntity> collect) {
        return personalGoodsRepository.batchSaveOrUpdate(collect);
    }
}
