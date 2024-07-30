package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.sparural.engine.api.dto.registration.BeginRegistrationRequest;
import ru.sparural.engine.api.dto.registration.ConfirmRegistrationRequest;
import ru.sparural.engine.api.dto.registration.RegistrationSetPasswordRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.entity.RoleNames;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserAction;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.services.AuthorizationService;
import ru.sparural.engine.services.RegistrationsService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.RegistrationStepException;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;
import ru.sparural.kafka.model.ServiceResponse;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class RegistrationController {

    private final UserService userService;
    private final RegistrationsService registrationsService;
    private final AuthorizationService authorizationService;

    /*
     * step 1
     */
    @KafkaSparuralMapping("registration/begin")
    public TokenDataDto beginRegistration(@Payload BeginRegistrationRequest beginRegistrationRequest) {
        var user = registrationsService.beginRegistration(beginRegistrationRequest);
        return authorizationService.generateUserRolesResponse(
                userService.createDto(user));
    }

    /*
     * step 2
     */
    @KafkaSparuralMapping("registration/confirm")
    public ServiceResponse confirmRegistration(@Payload ConfirmRegistrationRequest confirmRegistrationRequest,
                                               @RequestParam Long userId) {
        var result = registrationsService.confirmRegistration(confirmRegistrationRequest, userId);
        return ServiceResponse.builder()
                .success(result)
                .build();
    }

    /*
     * step 3
     */
    @KafkaSparuralMapping("registration/password")
    public ServiceResponse setPassword(@Payload RegistrationSetPasswordRequest setPasswordRequest,
                                       @RequestParam Long userId) {
        var result = registrationsService.setPassword(setPasswordRequest, userId);
        return ServiceResponse.builder()
                .success(result)
                .body(userId)
                .build();
    }

    /*
     * step 4, after success set to step 5
     */
    @KafkaSparuralMapping("registration/user")
    public TokenDataDto setUser(@Payload UserProfileUpdateRequest userRequest,
                                @RequestParam Long userId) {
        var savedUser = registrationsService.setUserInfo(userRequest, userId);
        return authorizationService.generateUserRolesResponse(
                userService.createDto(savedUser)
        );
    }

    @KafkaSparuralMapping("registration/send-confirm-code")
    public Boolean sendConfirmCode(@RequestParam Long userId) {
        return registrationsService.resendConfirmCode(userId);
    }

    @KafkaSparuralMapping("registration/check-step")
    public Integer checkCurrentStep(@RequestParam Long userId) {
        return registrationsService.checkStep(userId);
    }

    @KafkaSparuralMapping("registration/not-completed-list")
    public List<Long> notCompletedList(@RequestParam List<Long> definedUsers, Long group) {
        return registrationsService.findUsersWithNotCompletedRegistrations(definedUsers, group);
    }

}
