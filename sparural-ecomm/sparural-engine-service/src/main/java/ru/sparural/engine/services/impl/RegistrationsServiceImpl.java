package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.engine.api.dto.registration.*;
import ru.sparural.engine.entity.Registrations;
import ru.sparural.engine.entity.RoleNames;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.loymax.rest.dto.TokenExchangeResponse;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserAction;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfo;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserInfoSystem;
import ru.sparural.engine.loymax.rest.dto.user.LoymaxUserRegistrationStatus;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.loymax.utils.mappers.LoymaxUserInfoMapper;
import ru.sparural.engine.repositories.RegistrationsRepository;
import ru.sparural.engine.services.RegistrationsService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationsServiceImpl implements RegistrationsService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RegistrationsRepository registrationsRepository;
    private final UserService userService;
    private final LoymaxService loymaxService;

    private interface RegistrationThrowable {
        void throwRegException() throws RegistrationStepException;
    }

    /**
     * CREATES ENTITIES: User and LoymaxUser without access token for 'top level user'
     * <p>
     * We are checking if user exists in loymax,
     * if so then create new client user and 'loymax sub user' for top level user
     * <p>
     * To check user registration step - count user registration answers
     *
     * @param phoneNumber - user phone number
     * @return User - returns newly created user if user found in loymax
     * @throws UserNotFoundException     - if user not found in loymax
     * @throws RegistrationStepException - if user not finished registration in loymax
     */
    @Transactional
    @Override
    public User createRegisteredUserIfLoymaxUserRegistered(String phoneNumber, @Nullable TokenExchangeResponse tokenExchangeResponse)
            throws UserNotFoundException, RegistrationStepException {
        // check if exists in loymax, exit if not then check existing in our database
        var listInfo = loymaxService.getUserInfoSystem(phoneNumber);
        return createRegisteredUserIfLoymaxUserRegistered(listInfo, tokenExchangeResponse);
    }

    @Transactional
    @Override
    public User createRegisteredUserIfLoymaxUserRegistered(LoymaxUserInfoSystem loymaxUserInfoSystem, TokenExchangeResponse tokenResponse) {
        var userInfo = LoymaxUserInfoMapper.INSTANCE.loymaxDtoToEntity(loymaxUserInfoSystem);
        return createRegisteredUserIfLoymaxUserRegistered(userInfo, tokenResponse);
    }

    @Transactional
    @Override
    public User createRegisteredUserIfLoymaxUserRegistered(List<LoymaxUserInfoSystem> listInfo, @Nullable TokenExchangeResponse tokenExchangeResponse)
            throws UserNotFoundException, RegistrationStepException {
        // check if exists in loymax, exit if not then check existing in our database
        var userInfoSystem = listInfo.stream().findFirst()
                .orElseThrow(() -> new UserNotFoundException("No user data found while checking user data in loymax: " + listInfo));
        return createRegisteredUserIfLoymaxUserRegistered(userInfoSystem, tokenExchangeResponse);
    }

    private User createRegisteredUserIfLoymaxUserRegistered(LoymaxUserInfo userInfo, @Nullable TokenExchangeResponse tokenExchangeResponse)
            throws UserNotFoundException, RegistrationStepException {
        User targetUser;
        RegistrationThrowable exceptionRunnable;
        var phoneNumber = userInfo.getPhoneNumber();
        try {
            targetUser = userService.findByPhone(phoneNumber);
            loymaxService.createLoymaxUser(targetUser.getId(), userInfo, tokenExchangeResponse);
            var finalTargetUser = targetUser;
            exceptionRunnable = () -> {
                var currentStep = checkStep(finalTargetUser.getId());
                throw new RegistrationStepException(currentStep);
            };
        } catch (UserNotFoundException ignore) {
            targetUser = userService.createFromLoymaxDataAndSetClient(userInfo, phoneNumber, null);
            var anotherUser = userService.saveOrUpdate(targetUser);
            loymaxService.createLoymaxUser(anotherUser.getId(), userInfo, tokenExchangeResponse);
            targetUser = anotherUser;
            exceptionRunnable = () -> {
                throw new RegistrationStepException(0);
            };
        }

        if (checkIfRegistrationCompleted(userInfo.getState()))
            createOrUpdateRegistration(targetUser.getId(), StepConstants.COMPLETED.getStep());
        else exceptionRunnable.throwRegException();

        return targetUser;
    }

    private boolean checkIfRegistrationCompleted(String state) {
        return state.equals(LoymaxUserRegistrationStatus.REGISTERED.getValue());
    }

    @Transactional
    @Override
    public void createOrUpdateRegistration(Long userId, Integer step) {
        try {
            var reg = getByUserId(userId);
            reg.setStep(step);
            registrationsRepository.update(reg);
        } catch (ResourceNotFoundException ignored) {
            createRegistration(userId, step);
        }
    }

    @Transactional
    @Override
    public User beginRegistration(BeginRegistrationRequest beginRegistrationRequest) {
        var regException = new RegistrationStepException("Пользователь уже существует", 423);
        try {
            // check if user exists in loymax or database
            createRegisteredUserIfLoymaxUserRegistered(beginRegistrationRequest.getPhoneNumber(), null);
            throw regException;
        } catch (UserNotFoundException e) {
            log.info("Begin registration step - new user registration request: " + beginRegistrationRequest);
        }
        User user;
        try {
            user = userService.createAnonymous();
            loymaxService.beginLoymaxRegistration(beginRegistrationRequest.getPhoneNumber(), user.getId());
            // if everything is ok set registration step as '2' - wait for confirmation
            var reg = createRegistration(user.getId(), StepConstants.BEGIN.getStep());
            setNextRegistrationStep(reg, StepConstants.LOYMAX_CONFIRM);
        } catch (LoymaxException e) {
            log.error("LoymaxException: {}", e.getMessage());
            throw regException;
        }
        userService.createUnconfirmedPhoneRecord(beginRegistrationRequest.getPhoneNumber(), user.getId());
        return user;
    }

    /**
     * @param confirmRegistrationRequest - confirm code from loymax
     * @param userId                     - local user id
     */
    @Transactional
    @Override
    public Boolean confirmRegistration(ConfirmRegistrationRequest confirmRegistrationRequest, Long userId) {
        int currentStep = StepConstants.LOYMAX_CONFIRM.getStep();
        var registrations = checkIfRegistrationValid(currentStep, userId);
        loymaxService.confirmLoymaxRegistration(userId, confirmRegistrationRequest);
        var user = userService.findByUserId(userId);
        userService.confirmPhone(user);

        // after success switch to next step - 3 wait for set password
        setNextRegistrationStep(registrations, StepConstants.SET_PASSWORD);
        return true;
    }

    @Transactional
    @Override
    public Boolean setPassword(RegistrationSetPasswordRequest setPasswordRequest, Long userId) {
        int currentStep = StepConstants.SET_PASSWORD.getStep();
        var registrations = checkIfRegistrationValid(currentStep, userId);
        var user = userService.findByUserId(userId);
        loymaxService.registrationSetPasswordLoymax(setPasswordRequest, userId);
        user.setPassword(bCryptPasswordEncoder.encode(setPasswordRequest.getPassword()));
        userService.update(user);

        // after success switch to next step - 4 wait for set user info
        setNextRegistrationStep(registrations, StepConstants.SET_USER_INFO);
        return true;
    }

    @Transactional
    @Override
    public User setUserInfo(UserProfileUpdateRequest userRequest, Long userId) {
        if (userService.checkIfUserExistsWithPhoneOrEmail(userRequest.getEmail()))
            throw new ValidationException("Электронная почта уже существует");

        var currentStep = StepConstants.SET_USER_INFO.getStep();
        var registrations = checkIfRegistrationValid(currentStep, userId);

        loymaxService.sendRegistrationUserUpdateRequest(userRequest, userId);
        User user = userService.findByUserId(userId);
        user.setId(userId);
        userService.updateUserData(userRequest, user);
        userService.addRoleForUser(RoleNames.CLIENT.getName(), userId);
        userService.deleteRoleForUser(RoleNames.ANONYMOUS.getName(), userId);

        // after success switch to next step - 5 completed
        setNextRegistrationStep(registrations, StepConstants.COMPLETED);
        return userService.findByUserId(userId);
    }

    private void setNextRegistrationStep(Registrations registrations, StepConstants step) {
        registrations.setStep(step.getStep());
        registrationsRepository.update(registrations);
    }

    @Transactional
    @Override
    public Integer checkStep(Long userId) {
        try {
            // if registrations records exists in database
            var reg = getByUserId(userId);
            return reg.getStep();
        } catch (ResourceNotFoundException e) {
            var loymaxUser = loymaxService.getByLocalUserId(userId);
            var actions = loymaxService.getUserActions(loymaxUser);
            var actualStep = (int) actions.stream().filter(LoymaxUserAction::getIsDone).count();
            createRegistration(userId, actualStep);
            return actualStep;
        }
    }

    @Override
    public Boolean resendConfirmCode(Long userId) {
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        loymaxService.refreshTokenIfNeeded(loymaxUser);
        loymaxService.resendRegistrationConfirmCode(loymaxUser);
        return true;
    }

    private Registrations createRegistration(Long userId, Integer step) {
        var reg = new Registrations();
        reg.setUserId(userId);
        reg.setStep(step);
        var registrations = registrationsRepository.create(reg);
        return registrations.orElseThrow(() ->
                new RuntimeException("unexpected exception: cannot create registration for anonymous user"));
    }

    @Override
    public Registrations getByUserId(Long userId) {
        return registrationsRepository.getByUserId(userId)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public List<Registrations> getAll() {
        return registrationsRepository.getAll();
    }

    @Override
    public List<Long> findUsersWithNotCompletedRegistrations(List<Long> definedUsers, Long group) {
        return registrationsRepository.findUsersWithNotCompletedRegistrations(definedUsers, group)
                .stream().map(Registrations::getUserId).collect(Collectors.toList());
    }

    @Override
    public Registrations checkIfRegistrationValid(int step, Long userId) {
        var stepConst = StepConstants.getByStep(step);
        Registrations registrations = registrationsRepository.getByUserId(userId)
                .orElseThrow(() -> throwRegistrationStepException(stepConst));
        if (registrations.getStep() != step)
            throw throwRegistrationStepException(stepConst);
        return registrations;
    }

    private UnauthorizedException throwRegistrationStepException(StepConstants step) {
        log.error("confirm code registration error");
        return new UnauthorizedException(
                String.format("This action could perform at step %s of registration only", step), 403);
    }

}
