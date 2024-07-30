package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.engine.entity.MerchantFormat;
import ru.sparural.engine.repositories.MerchantFormatRepository;
import ru.sparural.engine.services.MerchantFormatService;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantFormatServiceImpl implements MerchantFormatService {
    private final MerchantFormatRepository repository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    public List<Format> list(Integer offset, Integer limit, List<String> nameNotEqual) {
        return createDtoList(repository.list(offset, limit, nameNotEqual));
    }

    @Override
    public List<Format> createDtoList(List<MerchantFormat> entities) {
        return dtoMapperUtils.convertList(Format.class, () -> entities);
    }

    @Override
    public Format get(Long id) {
        MerchantFormat entity = repository.get(id)
                .orElse(null);
        if (entity != null) {
            return createDtoFromEntity(entity);
        }
        return null;
    }

    @Override
    public Format createDtoFromEntity(MerchantFormat entity) {
        return dtoMapperUtils.convert(entity, Format.class);
    }

    @Override
    public MerchantFormat createEntityFromDto(Format dto) {
        return dtoMapperUtils.convert(dto, MerchantFormat.class);
    }

    @Override
    public Format checkIfExist(String name) {
        MerchantFormat merchantFormat = repository.getByName(name).orElse(null);
        if (merchantFormat == null) {
            MerchantFormat entity = new MerchantFormat();
            entity.setName(name);
            return create(entity);
        }
        return createDtoFromEntity(merchantFormat);
    }

    @Override
    public Format create(MerchantFormat entity) {
        return createDtoFromEntity(repository.create(entity)
                .orElse(null));
    }


}
