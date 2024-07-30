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

    private final LoymaxService loymaxService;
    private final UserService userService;
    private final RegistrationsService registrationsService;
    private final AuthorizationService authorizationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @KafkaSparuralMapping("registration/begin")
    public TokenDataDto beginRegistration(@Payload BeginRegistrationRequest beginRegistrationRequest) {
        var regException = new RegistrationStepException("Пользователь уже существует", 423);
        try {
            var u = userService.findByPhone(beginRegistrationRequest.getPhoneNumber());
            var loymaxUser = loymaxService.getByLocalUserId(u.getId());
            var actions = loymaxService.getUserActions(loymaxUser);
            int step = (int) actions.stream().filter(LoymaxUserAction::getIsDone).count();
            if (step < 4) {
                return authorizationService.generateUserRolesResponse(
                        userService.createDto(u)); //отправить 200 и шаг
            } else {
                throw regException;
            }
        } catch (UserNotFoundException e) {
            log.trace("Begin registration step - new user registration request");
        }

        // check if phone number is taken in 'loymax'
        if (loymaxService.checkIfNumberIsTaken(beginRegistrationRequest.getPhoneNumber()))
            throw regException;

        var user = userService.createAnonymous();
        Registrations reg = registrationsService.createRegistration(user.getId(), 1);
        try {
            loymaxService.beginRegistration(beginRegistrationRequest.getPhoneNumber(), reg, user.getId());
        } catch (LoymaxException e) {
            throw regException;
        }
        userService.createUnconfirmedPhoneRecord(beginRegistrationRequest.getPhoneNumber(), user.getId());
        return authorizationService.generateUserRolesResponse(
                userService.createDto(user));
    }

    @KafkaSparuralMapping("registration/confirm")
    public ServiceResponse confirmRegistration(@Payload ConfirmRegistrationRequest confirmRegistrationRequest,
                                               @RequestParam Long userId) {
        loymaxService.confirmRegistration(confirmRegistrationRequest, userId);
        var user = userService.findByUserId(userId);
        userService.confirmPhone(user);
        return ServiceResponse.builder()
                .success(true)
                .build();
    }

    @KafkaSparuralMapping("registration/password")
    public ServiceResponse setPassword(@Payload RegistrationSetPasswordRequest setPasswordRequest,
                                       @RequestParam Long userId) {
        User user = userService.findByUserId(userId);
        loymaxService.registrationSetPassword(setPasswordRequest, userId);
        user.setPassword(bCryptPasswordEncoder.encode(setPasswordRequest.getPassword()));
        userService.update(user);
        return ServiceResponse.builder()
                .success(true)
                .body(userId)
                .build();
    }

    @KafkaSparuralMapping("registration/user")
    public TokenDataDto setUser(@Payload UserProfileUpdateRequest userRequest,
                                @RequestParam Long userId) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(userRequest.getEmail())) {
            throw new ValidationException("Электронная почта уже существует");
        }
        loymaxService.sendRegistrationUserUpdateRequest(userRequest, userId);
        User user = userService.findByUserId(userId);
        user.setId(userId);
        userService.updateUserData(userRequest, user);
        userService.addRoleForUser(RoleNames.CLIENT.getName(), userId);
        userService.deleteRoleForUser(RoleNames.ANONYMOUS.getName(), userId);
        var saved = userService.findByUserId(userId);
        return authorizationService.generateUserRolesResponse(
                userService.createDto(saved)
        );
    }

    @KafkaSparuralMapping("registration/send-confirm-code")
    public Boolean sendConfirmCode(@RequestParam Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.resendRegistrationConfirmCode(loymaxUser);
        return true;
    }

    @KafkaSparuralMapping("registration/check-step")
    public Integer checkCurrentStep(@RequestParam Long userId) {
        var reg = registrationsService.getByUserId(userId);
        return reg.getStep();
    }

    @KafkaSparuralMapping("registration/not-completed-list")
    public List<Long> notCompletedList(@RequestParam List<Long> definedUsers, Long group) {
        return registrationsService.findUsersWithNotCompletedRegistrations(definedUsers, group);
    }

}
