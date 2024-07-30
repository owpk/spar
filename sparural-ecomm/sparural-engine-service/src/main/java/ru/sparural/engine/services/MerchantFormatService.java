package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.merchant.Format;
import ru.sparural.engine.entity.MerchantFormat;

import java.util.List;

public interface MerchantFormatService {
    List<Format> list(Integer offset, Integer limit, List<String> nameNotEqual);

    List<Format> createDtoList(List<MerchantFormat> entites);

    Format get(Long id);

    Format createDtoFromEntity(MerchantFormat entity);

    MerchantFormat createEntityFromDto(Format dto);

    Format create(MerchantFormat entity);

    List<MerchantFormat> batchSave(List<MerchantFormat> merchantFormats);

}
