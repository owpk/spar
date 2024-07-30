package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.settings.LoymaxSettingDto;
import ru.sparural.engine.entity.LoymaxSetting;
import ru.sparural.engine.loymax.services.LoymaxSettingsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class LoymaxSettingsController {

    private final LoymaxSettingsService loymaxSettingsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("loymax-settings/get")
    public LoymaxSettingDto get() {
        return mapperUtils.convert(LoymaxSettingDto.class, loymaxSettingsService::get);
    }

    @KafkaSparuralMapping("loymax-settings/update")
    public LoymaxSettingDto update(@Payload LoymaxSettingDto data) {
        return mapperUtils.convert(data, LoymaxSetting.class,
                loymaxSettingsService::update, LoymaxSettingDto.class);
    }
}
