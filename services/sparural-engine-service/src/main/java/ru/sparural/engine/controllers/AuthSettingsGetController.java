package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.settings.AuthSettingDto;
import ru.sparural.engine.entity.AuthSetting;
import ru.sparural.engine.services.AuthSettingsService;
import ru.sparural.engine.utils.DtoMapperUtils;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;

@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@RequiredArgsConstructor
public class AuthSettingsGetController {

    private final AuthSettingsService<AuthSetting> authSettingsService;
    private final DtoMapperUtils mapperUtils;

    @KafkaSparuralMapping("auth-settings/get")
    public AuthSettingDto get() {
        return mapperUtils.convert(AuthSettingDto.class, authSettingsService::get);
    }

    @KafkaSparuralMapping("auth-settings/update")
    public AuthSettingDto update(@Payload AuthSettingDto body) {
        return mapperUtils.convert(body, AuthSetting.class,
                authSettingsService::update, AuthSettingDto.class);
    }
}
