package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.PaymentSettingsDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.services.PaymentSettingsCacheService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/payment-settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "payment settings")
public class PaymentSettingsController {

    private final PaymentSettingsCacheService paymentSettingsCacheService;

    @Secured({RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN,
            RolesConstants.ROLE_MANAGER})
    @GetMapping
    public DataResponse<PaymentSettingsDto> get() {
        return new DataResponse<>(paymentSettingsCacheService.get());
    }


    @IsManagerOrAdmin
    @PutMapping
    public DataResponse<PaymentSettingsDto> update(
            @Valid @RequestBody DataRequest<PaymentSettingsDto> paymentSettingsDto
    ) {
        return paymentSettingsCacheService.update(paymentSettingsDto.getData());
    }

}
