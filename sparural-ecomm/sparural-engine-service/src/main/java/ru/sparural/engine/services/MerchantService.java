package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.MerchantDto;
import ru.sparural.engine.api.dto.MerchantUpdateDto;
import ru.sparural.engine.api.dto.merchant.Merchants;
import ru.sparural.engine.entity.Merchant;
import ru.sparural.engine.entity.MerchantFormat;

import javax.xml.bind.ValidationException;
import java.util.List;

public interface MerchantService {
    Merchants saveOrUpdate(MerchantDto createMerchantRequest);

    Merchant createEntityFromDto(MerchantDto createMerchantRequestDto) throws ValidationException;

    MerchantDto createDtoFromEntity(Merchant merchant);

    Merchants createDto(Merchant merchant);

    Merchants get(Long id, Long userId);

    Merchants getForChecks(Long id, Long userId);

    Boolean delete(Long id);

    Merchants update(Long id, MerchantUpdateDto updateDto);

    List<Merchants> list(Integer offset,
                         Integer limit,
                         Double topLeftLongitude,
                         Double topLeftLatitude,
                         Double bottomRightLongitude,
                         Double bottomRightLatitude,
                         Double userLongitude,
                         Double userLatitude,
                         String status,
                         Long[] format,
                         Long[] attributes,
                         Long userId,
                         List<String> roles);

    List<Merchants> convertListDto(List<Merchant> list);

    Merchants getFromLoymaxMerchant(String locationId);

    Merchants getByIdWithoutEx(Long id);

    List<MerchantFormat> batchSaveMerchantFormat(List<MerchantFormat> merchantFormats);

    List<Merchant> batchSave(List<Merchant> merchants);

}

