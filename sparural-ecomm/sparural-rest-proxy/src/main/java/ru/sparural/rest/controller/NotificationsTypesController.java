package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.services.NotificationsTypesCacheService;
import ru.sparural.rest.utils.Constants;

import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/notifications-types", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "notifications")
public class NotificationsTypesController {

    private final NotificationsTypesCacheService cacheService;

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_MANAGER})
    public DataResponse<List<NotificationsTypesDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                          @RequestParam(defaultValue = "30") Integer limit) {
        return DataResponse.<List<NotificationsTypesDto>>builder()
                .success(true)
                .data(cacheService.list(offset, limit))
                .version(Constants.VERSION)
                .build();
    }

}
