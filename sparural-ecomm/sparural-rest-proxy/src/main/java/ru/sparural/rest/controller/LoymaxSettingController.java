package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.settings.LoymaxSettingDto;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.LoymaxSettingsCacheService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/loymax-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "settings")
public class LoymaxSettingController {

    private final LoymaxSettingsCacheService loymaxSettingsCacheService;

    @GetMapping
    @IsManagerOrAdmin
    public DataResponse<LoymaxSettingDto> get() {
        return loymaxSettingsCacheService.get();
    }

    @PutMapping
    @IsManagerOrAdmin
    public DataResponse<LoymaxSettingDto> update(@Valid @RequestBody DataRequest<LoymaxSettingDto> restRequest) {
        return loymaxSettingsCacheService.update(restRequest.getData());
    }
}