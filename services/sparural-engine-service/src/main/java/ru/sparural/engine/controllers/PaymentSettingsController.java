package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.PaymentSettingsDto;
import ru.sparural.engine.services.PaymentSettingsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;


@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class PaymentSettingsController {

    private final PaymentSettingsService paymentSettingsService;

    @KafkaSparuralMapping("payment-settings/get")
    public PaymentSettingsDto getMerchantId() {
        return paymentSettingsService.getMerchantId();
    }

    @KafkaSparuralMapping("payment-setting/update")
    public PaymentSettingsDto updateMerchantId(@Payload PaymentSettingsDto paymentSettingsDto) {
        return paymentSettingsService.updateMerchantId(paymentSettingsDto.getTinkoffMerchantId());
    }
}
