package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.ExternalDocumentDto;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.Authenticated;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.ExternalDocumentsCacheService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/external-documents", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "external document")
public class ExternalDocumentController {

    private final ExternalDocumentsCacheService externalDocumentsCacheService;

    @GetMapping
    @Authenticated
    public DataResponse<List<ExternalDocumentDto>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                        @RequestParam(defaultValue = "30") Integer limit) {
        return externalDocumentsCacheService.list(offset, limit);
    }

    @GetMapping("/{alias}")
    public DataResponse<ExternalDocumentDto> get(@Valid @PathVariable String alias) {
        return externalDocumentsCacheService.get(alias);
    }

    @PostMapping
    @IsManagerOrAdmin
    public DataResponse<ExternalDocumentDto> create(@Valid @Parameter @RequestBody DataRequest<ExternalDocumentDto> restRequest) {
        return externalDocumentsCacheService.create(restRequest.getData());
    }

    @PutMapping("/{alias}")
    @IsManagerOrAdmin
    public DataResponse<ExternalDocumentDto> update(@Valid @Parameter @RequestBody DataRequest<ExternalDocumentDto> restRequest,
                                                    @PathVariable String alias) {
        return externalDocumentsCacheService.update(restRequest.getData(), alias);
    }

    @DeleteMapping("/{alias}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<Void> delete(@PathVariable String alias) {
        return externalDocumentsCacheService.delete(alias);
    }
}
