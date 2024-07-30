package ru.sparural.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import ru.sparural.engine.entity.PaymentSetting;
import ru.sparural.engine.repositories.PaymentSettingsRepository;
import ru.sparural.engine.utils.TimeHelper;
import ru.sparural.tables.PaymentSettings;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentSettingsRepositoryImpl implements PaymentSettingsRepository {
    private final DSLContext dslContext;

    @Override
    public Optional<PaymentSetting> getMerchantId() {
        return dslContext.selectFrom(PaymentSettings.PAYMENT_SETTINGS).fetchOptionalInto(PaymentSetting.class);
    }

    @Override
    public Optional<PaymentSetting> updateMerchantId(String merchantId) {
        return dslContext
                .update(PaymentSettings.PAYMENT_SETTINGS)
                .set(PaymentSettings.PAYMENT_SETTINGS.TINKOFF_MERCHANT_ID, merchantId)
                .set(PaymentSettings.PAYMENT_SETTINGS.UPDATED_AT, TimeHelper.currentTime())
                .where(PaymentSettings.PAYMENT_SETTINGS.ID.eq(1L))
                .returning()
                .fetchOptionalInto(PaymentSetting.class);
    }
}
