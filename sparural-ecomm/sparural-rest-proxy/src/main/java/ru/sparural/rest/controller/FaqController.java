package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.FaqDTO;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.FaqCacheService;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/faq", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "faq")
public class FaqController {

    private final FaqCacheService faqCacheService;

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_CLIENT, RolesConstants.ROLE_MANAGER})
    public DataResponse<List<FaqDTO>> list(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "30") Integer limit) {
        return DataResponse.<List<FaqDTO>>builder()
                .success(true)
                .data(faqCacheService.list(offset, limit))
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<FaqDTO> get(
            @PathVariable Long id) {
        return DataResponse.<FaqDTO>builder()
                .success(true)
                .data(faqCacheService.get(id))
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<FaqDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody DataRequest<FaqDTO> restRequest) {
        return DataResponse.<FaqDTO>builder()
                .success(true)
                .data(faqCacheService.update(id, restRequest.getData()))
                .version(Constants.VERSION)
                .build();
    }


    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        return UnwrappedGenericDto.<Void>builder()
                .success(faqCacheService.delete(id))
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<FaqDTO> create(@Valid @RequestBody DataRequest<FaqDTO> restRequest) {
        return DataResponse.<FaqDTO>builder()
                .success(true)
                .data(faqCacheService.create(restRequest.getData()))
                .version(Constants.VERSION)
                .build();
    }
}