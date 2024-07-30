package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.InfoScreenDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.InfoScreensCacheService;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/info-screens", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "info screens")
public class InfoScreensController {

    private final InfoScreensCacheService infoScreensCacheService;

    @GetMapping
    public DataResponse<List<InfoScreenDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                  @RequestParam(defaultValue = "30") Integer limit,
                                                  @RequestParam(required = false, defaultValue = "0") Integer city,
                                                  @RequestParam(required = false) Long dateStart,
                                                  @RequestParam(required = false) Long dateEnd,
                                                  @ApiIgnore UserPrincipal userPrincipal) {
        boolean showOnlyPublic = false;
        if (userPrincipal != null) {
            var roles = userPrincipal.getSecuredRoles();
            var isAdmin = roles.stream().anyMatch(x -> x.equals(RolesConstants.ROLE_ADMIN)
                    || x.equals(RolesConstants.ROLE_MANAGER));
            var isClient = roles.stream().anyMatch(x -> x.equals(RolesConstants.ROLE_CLIENT)
                    || x.equals(RolesConstants.ROLE_ANONYMOUS));
            if (!isAdmin && isClient) {
                showOnlyPublic = true;
            }
        } else {
            showOnlyPublic = true;
        }
        return infoScreensCacheService.list(offset, limit, city, showOnlyPublic, dateStart, dateEnd);
    }

    @GetMapping("/{id}")
    public DataResponse<InfoScreenDto> get(@PathVariable Long id) {
        return infoScreensCacheService.get(id);
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<InfoScreenDto> create(@Valid @Parameter @RequestBody DataRequest<InfoScreenDto> restRequest) {
        return infoScreensCacheService.create(restRequest.getData());
    }

    @PutMapping("/{id}")
    @IsManagerOrAdmin
    public DataResponse<InfoScreenDto> update(@Valid @Parameter @RequestBody DataRequest<InfoScreenDto> restRequest,
                                              @PathVariable Long id) {
        return infoScreensCacheService.update(restRequest.getData(), id);
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        infoScreensCacheService.delete(id);
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(true).build();
    }
}