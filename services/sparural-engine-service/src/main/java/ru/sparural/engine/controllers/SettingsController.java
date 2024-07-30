package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.settings.SettingDto;
import ru.sparural.engine.entity.Setting;
import ru.sparural.engine.services.SettingsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class SettingsController {

    private final SettingsService<Setting> settingsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("settings/get")
    public SettingDto get() {
        return mapperUtils.convert(SettingDto.class, settingsService::get);
    }

    @KafkaSparuralMapping("settings/update")
    public SettingDto update(@Payload SettingDto settingDto) {
        return mapperUtils.convert(settingDto, Setting.class, settingsService::update, SettingDto.class);
    }
}
