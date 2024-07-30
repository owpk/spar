package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.settings.ConfirmCodeSettingDto;
import ru.sparural.engine.entity.ConfirmCodeSetting;
import ru.sparural.engine.services.ConfirmCodeSettingsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class ConfirmCodeSettingsController {

    private final ConfirmCodeSettingsService<ConfirmCodeSetting> confirmCodeSettingsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("confirm-code-settings/get")
    public ConfirmCodeSettingDto get() {
        return mapperUtils.convert(ConfirmCodeSettingDto.class, confirmCodeSettingsService::get);
    }

    @KafkaSparuralMapping("confirm-code-settings/update")
    public ConfirmCodeSettingDto update(@Payload ConfirmCodeSettingDto confirmCodeSettingDto) {
        return mapperUtils.convert(confirmCodeSettingDto, ConfirmCodeSetting.class,
                confirmCodeSettingsService::update, ConfirmCodeSettingDto.class);
    }
}
