package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.PaymentSetting;

import java.util.Optional;

public interface PaymentSettingsRepository {
    Optional<PaymentSetting> getMerchantId();

    Optional<PaymentSetting> updateMerchantId(String merchantId);
}
