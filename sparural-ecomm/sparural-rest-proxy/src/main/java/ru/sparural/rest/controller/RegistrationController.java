package ru.sparural.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.sparural.engine.api.dto.registration.BeginRegistrationRequest;
import ru.sparural.engine.api.dto.registration.ConfirmRegistrationRequest;
import ru.sparural.engine.api.dto.registration.RegistrationSetPasswordRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.gobals.Constants;
import ru.sparural.gobals.RolesConstants;
import ru.sparural.kafka.model.ServiceResponse;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.dto.ConfirmRegistrationRest;
import ru.sparural.rest.dto.DataRequest;
import ru.sparural.rest.exception.RestRequestException;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.security.annotations.Authenticated;
import ru.sparural.rest.services.AuthorizationService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.controller.advice.annotations.ResponseType;
import ru.sparural.rest.dto.JwtResponse;
import ru.sparural.rest.security.TokenManager;

/**
 * @author Vorobyev Vyacheslav
 */
@RestController
@RequestMapping(value = "${rest.base-url}/${rest.version}/registration", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "registration")
public class RegistrationController {

    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;
    private final AuthorizationService authorizationService;
    private final TokenManager tokenManager;

    @PostMapping("/begin")
    @ApiOperation(value = "begin registration")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse beginRegistration(@Valid @Parameter @RequestBody BeginRegistrationRequest beginRegistrationRequest,
                                               @ApiIgnore HttpServletResponse response,
                                               @RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
                                                       defaultValue = Constants.CLIENT_TYPE_MOBILE) String client,
                                               @ApiIgnore UserPrincipal principal) {
        if (principal != null)
            throw new RestRequestException("user already authorized", 403);


        var token = authorizationService.beginRegistration(beginRegistrationRequest);
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }

        return token;
    }

    @PostMapping("/confirm")
    @Authenticated
    @ApiOperation(value = "begin registration", authorizations = {
            @Authorization(RolesConstants.ROLE_ANONYMOUS),
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    @ResponseType(ControllerResponseType.RAW)
    public ServiceResponse confirmRegistration(@Valid @Parameter @RequestBody ConfirmRegistrationRest confirmRegistrationRest,
                                               @ApiIgnore UserPrincipal principal) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/confirm")
                .withRequestParameter("userId", principal.getUserId())
                .withRequestBody(new ConfirmRegistrationRequest(confirmRegistrationRest.getCode()))
                .sendForEntity();
    }

    @PostMapping("/password")
    @Authenticated
    @ApiOperation(value = "set password", authorizations = {
            @Authorization(RolesConstants.ROLE_ANONYMOUS),
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    @ResponseType(ControllerResponseType.RAW)
    public ServiceResponse SetPassword(@Valid @Parameter @RequestBody RegistrationSetPasswordRequest request,
                                       @ApiIgnore UserPrincipal principal) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/password")
                .withRequestParameter("userId", principal.getUserId())
                .withRequestBody(request)
                .sendForEntity();
    }

    @PostMapping("/user")
    @Authenticated
    @ApiOperation(value = "update user info", authorizations = {
            @Authorization(RolesConstants.ROLE_ANONYMOUS),
            @Authorization(RolesConstants.ROLE_CLIENT),
            @Authorization(RolesConstants.ROLE_ADMIN),
            @Authorization(RolesConstants.ROLE_MANAGER),
    })
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public JwtResponse loymaxUpdateUserInfo(@Valid @Parameter @RequestBody DataRequest<UserProfileUpdateRequest> userDto,
                                                  @ApiIgnore UserPrincipal principal,
                                                  @ApiIgnore HttpServletResponse response,
                                                  @RequestHeader(value = Constants.CLIENT_TYPE_HEADER_NAME,
                                                          defaultValue = Constants.CLIENT_TYPE_MOBILE) String client) {

        var token = authorizationService.updateUserInfo(principal.getUserId(), userDto.getData());
        if (Constants.CLIENT_TYPE_WEB.equals(client)) {
            response.addCookie(tokenManager.createSecuredCookie(Constants.ACCESS_TOKEN, token.getAccessToken(), tokenManager.getJwtExpiresIn()));
            response.addCookie(tokenManager.createSecuredCookie(Constants.REFRESH_TOKEN, token.getRefreshToken(), tokenManager.getRefreshExpiresIn()));
        }

        return token;
    }

    @PostMapping("/send-confirm-code")
    @Authenticated
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public void sendConfirmCode(@ApiIgnore UserPrincipal userPrincipal) {
        Boolean success = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/send-confirm-code")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();

        if (!success) {
            throw new RestRequestException();
        }
    }

    @Authenticated
    @GetMapping("/check-step")
    @ResponseType(ControllerResponseType.UNWRAPPED)
    public Integer checkUserCurrentStep(@ApiIgnore UserPrincipal userPrincipal) {
        return restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("registration/check-step")
                .withRequestParameter("userId", userPrincipal.getUserId())
                .sendForEntity();
    }
}