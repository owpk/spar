package ru.sparural.engine.services;


import ru.sparural.engine.api.dto.PaymentSettingsDto;
import ru.sparural.engine.entity.PaymentSetting;

public interface PaymentSettingsService {
    PaymentSettingsDto getMerchantId();

    PaymentSettingsDto updateMerchantId(String merchantId);

    PaymentSetting createEntityFromDTO(PaymentSettingsDto paymentSettingsDto);

    PaymentSettingsDto createDTOFromEntity(PaymentSetting paymentSetting);
}
