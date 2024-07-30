package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.entity.LoymaxSetting;
import ru.sparural.engine.loymax.services.LoymaxSettingsService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class CaptchaController {

    private final LoymaxSettingsService loymaxSettingsService;

    @KafkaSparuralMapping("captcha-site-key/get")
    public String getSiteKey() {
        LoymaxSetting loymaxSetting = loymaxSettingsService.get();
        return loymaxSetting.getSiteKey();
    }
}
