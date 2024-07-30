package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.ContentDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsAdmin;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.services.ContentsCacheService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/contents", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "contents")
public class ContentsController {

    private final ContentsCacheService contentsCacheService;

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_CLIENT, RolesConstants.ROLE_MANAGER})
    public DataResponse<List<ContentDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                               @RequestParam(defaultValue = "30") Integer limit) {
        return contentsCacheService.list(offset, limit);
    }

    @GetMapping("/{alias}")
    public DataResponse<ContentDto> get(@NotBlank(message = "Input alias") @PathVariable String alias) {
        return contentsCacheService.get(alias);
    }

    @PostMapping
    @IsAdmin
    public DataResponse<ContentDto> create(@Valid @Parameter @RequestBody DataRequest<ContentDto> restRequest) {
        return contentsCacheService.create(restRequest.getData());
    }

    @PutMapping("/{alias}")
    @IsAdmin
    public DataResponse<ContentDto> update(@Valid @Parameter @RequestBody DataRequest<ContentDto> restRequest,
                                           @PathVariable String alias) {
        return contentsCacheService.update(restRequest.getData(), alias);
    }

    @DeleteMapping("/{alias}")
    @IsAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@NotBlank(message = "Input alias") @PathVariable String alias) {
        return contentsCacheService.delete(alias);
    }

}