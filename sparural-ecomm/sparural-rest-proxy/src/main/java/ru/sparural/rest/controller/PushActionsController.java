package ru.sparural.rest.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.ScreenDto;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.PushActionsCacheService;

import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/push-actions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "push actions")
public class PushActionsController {

    private final PushActionsCacheService pushActionsCacheService;

    @IsManagerOrAdmin
    @GetMapping
    public DataResponse<List<ScreenDto>> index(Integer offset, Integer limit) {
        return pushActionsCacheService.index(offset, limit);
    }
}
