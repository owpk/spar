package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.OnboxBannerDto;
import ru.sparural.engine.api.dto.OnboxBannerForUpdateDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsAdmin;
import ru.sparural.rest.services.OnboxBannersCacheService;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import ru.sparural.rest.config.KafkaTopics;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/onbox-banners", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "onbox banner")
public class OnboxBannerController {

    private final OnboxBannersCacheService onboxBannersCacheService;
    private final KafkaTopics kafkaTopics;
    private final SparuralKafkaRequestCreator restToKafkaService;

    @GetMapping
    public DataResponse<List<OnboxBannerDto>> list(@RequestParam(defaultValue = "0") Integer offset,
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
            var isClient = roles.stream().anyMatch(x -> x.equals(RolesConstants.ROLE_CLIENT) || x.equals(RolesConstants.ROLE_ANONYMOUS));
            if (!isAdmin && isClient) {
                showOnlyPublic = true;
            }
        } else {
            showOnlyPublic = true;
        }

        List<OnboxBannerDto> response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("onbox-banners/list")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .withRequestParameter("city", city)
                .withRequestParameter("showPublic", showOnlyPublic)
                .withRequestParameter("dateStart", dateStart)
                .withRequestParameter("dateEnd", dateEnd)
                .sendForEntity();
        var sorted = response.stream()
                .sorted(Comparator.comparing(x -> {
                    var value = (Integer) x.getOrder();
                    return Objects.requireNonNullElse(value, 0);
                }))
                .collect(Collectors.toList());
        return DataResponse.<List<OnboxBannerDto>>builder()
                .data(sorted)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<OnboxBannerDto> get(@PathVariable Long id) {
        return onboxBannersCacheService.get(id);
    }

    @PostMapping
    @IsAdmin
    public DataResponse<OnboxBannerDto> create(@Valid @Parameter @RequestBody DataRequest<OnboxBannerDto> restRequest) {
        return onboxBannersCacheService.create(restRequest.getData());
    }

    @PutMapping("/{id}")
    @IsAdmin
    public DataResponse<OnboxBannerDto> update(@Valid @Parameter @RequestBody DataRequest<OnboxBannerForUpdateDto> restRequest,
                                               @PathVariable Long id) {
        return onboxBannersCacheService.update(restRequest.getData(), id);
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        return onboxBannersCacheService.delete(id);
    }

}
