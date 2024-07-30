package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.*;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.user.ChangePasswordRequestDto;
import ru.sparural.engine.api.dto.user.RoleDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.EmptyObject;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.Authenticated;
import ru.sparural.rest.security.annotations.IsClient;
import ru.sparural.rest.security.annotations.IsManagerOrAdmin;
import ru.sparural.rest.security.annotations.NotAllowedForAnonymous;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import ru.sparural.rest.config.KafkaTopics;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/user", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "users")
public class UserController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @Authenticated
    @GetMapping("/get-id")
    public DataResponse<Long> getUserId(@ApiIgnore UserPrincipal userPrincipal) {
        return DataResponse.<Long>builder()
                .data(userPrincipal.getUserId())
                .success(true)
                .build();
    }

    @IsClient
    @PostMapping("/deregistration/send-confirm-code")
    public DataResponse<EmptyObject> unregisterSendConfirmCode(@ApiIgnore UserPrincipal userPrincipal) {
        restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withAction("user/deregistration-send-confirm-code")
                .sendForEntity();
        return DataResponse.<EmptyObject>builder()
                .success(true).build();
    }

    @IsClient
    @PostMapping("/deregistration/confirm")
    public DataResponse<EmptyObject> unregisterConfirm(@ApiIgnore UserPrincipal userPrincipal,
                                                       @RequestBody DeregistrationConfirm deregistrationConfirm) {
        restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/deregistration-confirm")
                .withRequestBody(deregistrationConfirm)
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return DataResponse.<EmptyObject>builder()
                .success(true).build();
    }

    @PostMapping("/change-reject-paper-checks")
    @ApiOperation(value = "change reject paper checks plan")
    @IsClient
    public DataResponse<UserProfileDto> rejectPaperChecks(@Valid @RequestBody RejectPaperCheckDto rejectPaperCheckDto,
                                                          @ApiIgnore UserPrincipal principal) {
        UserProfileDto success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestBody(rejectPaperCheckDto)
                .withAction("user/change-reject-paper-checks")
                .withRequestBody(rejectPaperCheckDto)
                .withRequestParameter("userId", principal.getUserId())
                .sendForEntity();
        return DataResponse.<UserProfileDto>builder()
                .data(success)
                .success(true)
                .build();
    }

    @NotAllowedForAnonymous
    @PostMapping("/change-password")
    @ApiOperation(value = "change password", authorizations = {
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public UnwrappedGenericDto<EmptyObject> changePassword(@Valid @RequestBody ChangePasswordRequestDto changePasswordRequest,
                                                           @ApiIgnore UserPrincipal principal) {
        if (changePasswordRequest.getNewPassword().equals(changePasswordRequest.getOldPassword()))
            throw new ValidationException("The new password must not match the old one");
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withRequestBody(changePasswordRequest)
                .withAction("user/change-password")
                .withRequestParameter("userId", principal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(success).build();
    }

    @Deprecated
    @NotAllowedForAnonymous
    @GetMapping
    @ResponseType(ControllerResponseType.RAW)
    @ApiOperation(value = "get user profile", authorizations = {
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public UserProfileDto getUserData(@ApiIgnore UserPrincipal principal) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/get")
                .withRequestParameter("userId", principal.getUserId())
                .sendForEntity();
    }


    @NotAllowedForAnonymous
    @PostMapping
    @ApiOperation(value = "update user profile", authorizations = {
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public UserProfileDto updateUserData(@ApiIgnore UserPrincipal principal,
                                         @Valid @RequestBody UserProfileUpdateRequest userProfileUpdateRequest) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/update")
                .withRequestParameter("userId", principal.getUserId())
                .withRequestBody(userProfileUpdateRequest)
                .sendForEntity();
    }

    @IsManagerOrAdmin
    @GetMapping("/roles")
    @ApiOperation(value = "get user roles", authorizations = {
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public DataResponse<List<RoleDto>> getUserData() {
        List<RoleDto> resp = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/roles")
                .sendForEntity();
        return DataResponse.<List<RoleDto>>builder()
                .success(true)
                .data(resp)
                .version(1).build();
    }

    @Authenticated
    @GetMapping("/check")
    @ApiOperation(value = "check user profile", authorizations = {
            @Authorization(RolesConstants.ROLE_ANONYMOUS),
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    public UnwrappedGenericDto<EmptyObject> check() {
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(true)
                .build();
    }

    @PostMapping("/push-token")
    public DataResponse<PushTokenReqDto> get(@Valid @RequestBody PushTokenReqDto restRequest,
                                             @ApiIgnore UserPrincipal principal) {
        PushTokenReqDto pushTokenReqDto = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/save-push-token")
                .withRequestBody(restRequest)
                .withRequestParameter("userId", principal == null ? 0 : principal.getUserId())
                .sendForEntity();
        return new DataResponse<>(restRequest);
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/email")
    public UnwrappedGenericDto<EmptyObject> updateEmail(@ApiIgnore UserPrincipal userPrincipal,
                                                        @Valid @RequestBody EmailDto emailDto) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/update-email")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestBody(emailDto)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/email/confirm")
    public UnwrappedGenericDto<EmptyObject> confirmEmail(@ApiIgnore UserPrincipal userPrincipal,
                                                         @Valid @RequestBody CodeDto codeDto) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/email-confirm")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestBody(codeDto)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/email/send-confirm-code")
    public UnwrappedGenericDto<EmptyObject> sendConfirmCode(@ApiIgnore UserPrincipal userPrincipal) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/email-send-confirm-code")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/phone-number")
    public UnwrappedGenericDto<EmptyObject> updatePhoneNumber(@ApiIgnore UserPrincipal userPrincipal,
                                                              @Valid @RequestBody PhoneDto phoneDto) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/update-phone-number")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestBody(phoneDto)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @GetMapping("/phone/cancel-update")
    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    public UnwrappedGenericDto<EmptyObject> cancelUpdatingPhoneNumber(@ApiIgnore UserPrincipal userPrincipal) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/cancel-update-phone-number")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/phone/confirm")
    public UnwrappedGenericDto<EmptyObject> confirmPhone(@ApiIgnore UserPrincipal userPrincipal,
                                                         @Valid @RequestBody CodeDto codeDto) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/phone-confirm")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .withRequestBody(codeDto)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }

    @Secured({
            RolesConstants.ROLE_MANAGER,
            RolesConstants.ROLE_CLIENT,
            RolesConstants.ROLE_ADMIN
    })
    @PostMapping("/phone/send-confirm-code")
    public UnwrappedGenericDto<EmptyObject> sendConfirmCodeUpdatePhone(@ApiIgnore UserPrincipal userPrincipal) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("user/phone-send-confirm-code")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }
}