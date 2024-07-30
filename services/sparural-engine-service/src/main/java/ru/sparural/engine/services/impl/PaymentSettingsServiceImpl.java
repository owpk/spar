package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.PaymentSettingsDto;
import ru.sparural.engine.entity.PaymentSetting;
import ru.sparural.engine.repositories.PaymentSettingsRepository;
import ru.sparural.engine.services.PaymentSettingsService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.utils.DtoMapperUtils;


@Service
@RequiredArgsConstructor
public class PaymentSettingsServiceImpl implements PaymentSettingsService {

    private final DtoMapperUtils mapperUtils;
    private final PaymentSettingsRepository paymentSettingsRepository;

    @Override
    public PaymentSetting createEntityFromDTO(PaymentSettingsDto paymentSettingsDto) {
        return mapperUtils.convert(paymentSettingsDto, PaymentSetting.class);
    }

    @Override
    public PaymentSettingsDto createDTOFromEntity(PaymentSetting paymentSetting) {
        return mapperUtils.convert(paymentSetting, PaymentSettingsDto.class);
    }

    @Override
    public PaymentSettingsDto getMerchantId() {
        return createDTOFromEntity(paymentSettingsRepository.getMerchantId().orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }

    @Override
    public PaymentSettingsDto updateMerchantId(String merchantId) {
        return createDTOFromEntity(paymentSettingsRepository.updateMerchantId(merchantId).orElseThrow(() -> new ResourceNotFoundException("Resource not found")));
    }
}
