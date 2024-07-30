package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.UserRequestsSubjectsDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.annotations.Authenticated;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.UserRequestsSubjectsCacheService;
import ru.sparural.rest.utils.Constants;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/user-requests-subjects", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "user-requests")
public class UserRequestsSubjectsController {

    private final UserRequestsSubjectsCacheService userRequestsSubjectsCacheService;

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_CLIENT, RolesConstants.ROLE_MANAGER})
    public DataResponse<List<UserRequestsSubjectsDto>> get(@RequestParam(defaultValue = "0") Integer offset,
                                                           @RequestParam(defaultValue = "30") Integer limit) {
        return DataResponse.<List<UserRequestsSubjectsDto>>builder()
                .success(true)
                .data(userRequestsSubjectsCacheService.list(offset, limit))
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<UserRequestsSubjectsDto> create(@Valid @RequestBody DataRequest<UserRequestsSubjectsDto> request) {
        return userRequestsSubjectsCacheService.create(request.getData());
    }


    @GetMapping("/{id}")
    @IsClient
    public DataResponse<UserRequestsSubjectsDto> get(@PathVariable Long id) {
        return userRequestsSubjectsCacheService.get(id);
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        return userRequestsSubjectsCacheService.delete(id);
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<UserRequestsSubjectsDto> update(@PathVariable Long id, @Valid @RequestBody DataRequest<UserRequestsSubjectsDto> request) {
        return userRequestsSubjectsCacheService.update(id, request.getData());
    }
}
