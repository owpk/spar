package ru.sparural.engine.services;

import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.registration.BeginRegistrationRequest;
import ru.sparural.engine.api.dto.registration.ConfirmRegistrationRequest;
import ru.sparural.engine.api.dto.registration.RegistrationSetPasswordRequest;
import ru.sparural.engine.api.dto.registration.UserProfileUpdateRequest;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfoSystem;
import ru.sparural.engine.services.exception.RegistrationStepException;
import ru.sparural.engine.services.exception.UserNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RegistrationsService {
    Registrations checkIfRegistrationValid(int step, Long userId);

    @Transactional
    User createRegisteredUserIfLoymaxUserRegistered(List<LoymaxUserInfoSystem> listInfo, @Nullable TokenExchangeResponse tokenExchangeResponse) throws UserNotFoundException, RegistrationStepException;

    void createOrUpdateRegistration(Long userId, Integer step);

    /*
     *  @return registration step
     */
    User beginRegistration(BeginRegistrationRequest beginRegistrationRequest);

    Boolean confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest, Long userId);

    Registrations getByUserId(Long userId);

    List<Registrations> getAll();

    List<Long> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long group);

    Boolean setPassword(RegistrationSetPasswordRequest setPasswordRequest, Long userId);

    User setUserInfo(UserProfileUpdateRequest userRequest, Long userId);

    Integer checkStep(Long userId);

    Boolean resendConfirmCode(Long userId);

    User createRegisteredUserIfLoymaxUserRegistered(String phoneNumber, TokenExchangeResponse tokenResponse);

    User createRegisteredUserIfLoymaxUserRegistered(LoymaxUserInfoSystem loymaxUserInfoSystem, TokenExchangeResponse tokenResponse);
}
