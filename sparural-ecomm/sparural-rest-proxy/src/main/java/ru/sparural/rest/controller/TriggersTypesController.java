package ru.sparural.rest.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.TriggersTypesCacheService;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;

import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/triggers-types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "triggers")
public class TriggersTypesController {

    private final TriggersTypesCacheService triggersTypesCacheService;

//    @IsManagerOrAdmin
    @GetMapping
    public DataResponse<List<TriggersTypeDTO>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit) {
        return triggersTypesCacheService.list(offset, limit);
    }

}
