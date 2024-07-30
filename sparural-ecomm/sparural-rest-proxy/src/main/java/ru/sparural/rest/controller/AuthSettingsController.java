package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.settings.AuthSettingDto;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.AuthSettingsCacheService;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/auth-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "settings")
public class AuthSettingsController {

    private final AuthSettingsCacheService authSettingsCacheService;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<AuthSettingDto> get() {
        return authSettingsCacheService.get();
    }

    @PutMapping
    @IsManagerOrAdmin
    public DataResponse<AuthSettingDto> update(@RequestBody DataRequest<AuthSettingDto> requestDto) {
        return authSettingsCacheService.update(requestDto.getData());
    }
}