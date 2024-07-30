package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.DeliveryCreateDTO;
import ru.sparural.engine.api.dto.DeliveryDTO;
import ru.sparural.engine.api.dto.DeliveryUpdateDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.DeliveryCacheService;
import ru.sparural.rest.utils.Constants;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@EqualsAndHashCode
@RestController
@RequestMapping(value = "${rest.base-url}/v2/delivery", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "delivery")
public class DeliveryControllerV2 {

    private final DeliveryCacheService deliveryCacheService;

    @GetMapping
    public DataResponse<List<DeliveryDTO>> list(@RequestParam(defaultValue = "0") Integer offset,
                                                @RequestParam(defaultValue = "30") Integer limit,
                                                @ApiIgnore UserPrincipal userPrincipal) {
        var showAll = false;
        if (userPrincipal != null) {
            var roles = userPrincipal.getSecuredRoles();
            showAll = (roles.contains(RolesConstants.ROLE_ADMIN) || roles.contains(RolesConstants.ROLE_MANAGER));
        }
        return DataResponse.<List<DeliveryDTO>>builder()
                .success(true)
                .data(deliveryCacheService.listV2(offset, limit, showAll))
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<DeliveryDTO> get(@PathVariable Long id) {
        return DataResponse.<DeliveryDTO>builder()
                .success(true)
                .data(deliveryCacheService.get(id))
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @DeleteMapping("/{id}")
    public UnwrappedGenericDto<Void> delete(@PathVariable Long id) {
        return UnwrappedGenericDto.<Void>builder()
                .success(deliveryCacheService.delete(id))
                .build();
    }

    @IsManagerOrAdmin
    @PutMapping("/{id}")
    public DataResponse<DeliveryDTO> update(@PathVariable Long id,
                                            @Valid @RequestBody DataRequest<DeliveryUpdateDto> deliveryDTO) {
        return DataResponse.<DeliveryDTO>builder()
                .success(true)
                .data(deliveryCacheService.update(id, deliveryDTO.getData()))
                .version(Constants.VERSION)
                .build();
    }

    @IsManagerOrAdmin
    @PostMapping
    public DataResponse<DeliveryDTO> create(@Valid @RequestBody DataRequest<DeliveryCreateDTO> deliveryCreateDTO) {
        var data = deliveryCacheService.create(deliveryCreateDTO.getData());
        return DataResponse.<DeliveryDTO>builder()
                .success(true)
                .data(data)
                .version(Constants.VERSION)
                .build();
    }
}
