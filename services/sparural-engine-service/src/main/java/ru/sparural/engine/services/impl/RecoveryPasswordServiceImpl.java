package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.RecoveryPasswordConfirmCodeRequestDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.RecoveryTokenResponseDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.enums.NotifierIdentityNames;
import ru.sparural.engine.entity.LoymaxUser;
import ru.sparural.engine.entity.RoleNames;
import ru.sparural.engine.entity.User;
import ru.sparural.engine.loymax.rest.dto.password.LoymaxResetPasswordRequest;
import ru.sparural.engine.loymax.services.impl.LoymaxServiceImpl;
import ru.sparural.engine.services.RecoveryPasswordService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.UnauthorizedException;
import ru.sparural.engine.services.exception.UserNotFoundException;
import ru.sparural.kafka.exception.KafkaControllerException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class RecoveryPasswordServiceImpl implements RecoveryPasswordService {
    private final LoymaxServiceImpl loymaxService;
    private final PasswordRecoveryRequestService passwordRecoveryRequestService;
    private final UserServiceImpl userService;
    private final AuthorizationServiceImpl authorizationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleServiceImpl roleService;

    /**
     * В таблице recoveryPasswordRequests создается запись о восстановлении пароля. Token в формате uuid. Время жизни токена 3 суток.
     * Если пользователь с ролью client, в Loymax отправляется запрос на восстановление пароля
     * Если пользователь с другой ролью, то код отправляется через микросервис уведомлений.
     *
     * @throws KafkaControllerException e
     */
    @Override
    public RecoveryTokenResponseDto recover(RecoveryPasswordRequestDto recoveryPasswordRequestDto) throws KafkaControllerException {
        UserDto userDto = null;
        try {
            if (recoveryPasswordRequestDto.getNotifier().equals(NotifierIdentityNames.PHONE.getValue())) {
                userDto = userService.createDto(
                        userService.findByPhone(
                                recoveryPasswordRequestDto.getNotifierIdentity()));
            } else if (recoveryPasswordRequestDto.getNotifier().equals(NotifierIdentityNames.EMAIL.getValue()))
                userDto = userService.createDto(
                        userService.findByEmail(
                                recoveryPasswordRequestDto.getNotifierIdentity()));
            else throw new KafkaControllerException("unknown identity", 400);
        } catch (UserNotFoundException ignore) {
        }

//        if (userDto != null && userDto.getRoles().stream().anyMatch(x -> !x.getCode().equals(RoleNames.CLIENT.getName())))
//            throw new StatusException("Not supported for not client user", 400);

        if (userDto == null) {
            User user = new User();
            if (recoveryPasswordRequestDto.getNotifier().equals(NotifierIdentityNames.PHONE.getValue())) {
                user.setPhoneNumber(recoveryPasswordRequestDto.getNotifierIdentity());
                userDto = userService.createDto(userService.saveOrUpdate(user));
                userService.addRoleForUser(RoleNames.CLIENT.getName(), userDto.getId());
            } else if (recoveryPasswordRequestDto.getNotifier().equals(NotifierIdentityNames.EMAIL.getValue())) {
                user.setEmail(recoveryPasswordRequestDto.getNotifierIdentity());
                userDto = userService.createDto(userService.saveOrUpdate(user));
                userService.addRoleForUser(RoleNames.CLIENT.getName(), userDto.getId());
            }
        }
        loymaxService.recoverPassword(recoveryPasswordRequestDto);

        String uuid = UUID.randomUUID().toString();
        var recoveryDto = new RecoveryTokenResponseDto();
        recoveryDto.setRecoveryToken(uuid);
        passwordRecoveryRequestService
                .createPasswordRecoveryRequestRecord(
                        recoveryPasswordRequestDto, uuid, userDto == null ? null : userDto.getId());
        return recoveryDto;
    }

    /**
     * Проверка, существует ли такой recoveryToken в таблице recoveryPasswordRequests и не истекло ли его время жизни.
     * Если пользователь с ролью client
     * Отправляется запрос в Loymax.
     * Если от Loymax ответ успешен, проверка, существует ли такой пользователь в нашей БД.
     * Если такой пользователь не существует, происходит создание его с ролью client.
     * Возвращается все необходимые данные для генерации токенов.
     * Если пользователь с другой рольлю
     * Проверяется корректность кода
     * Если код верен, то возвращается все необходимые данные для генерации токенов.
     */
    @Override
    public TokenDataDto recoveryConfirm(RecoveryPasswordConfirmCodeRequestDto confirmCodeRequestDto) {
        var recoveryDto = passwordRecoveryRequestService.getByToken(confirmCodeRequestDto.getRecoveryToken());
        Long expiredAt = recoveryDto.getExpired();
        if ((expiredAt - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) > 0)) {
            var tokenExchangeResponse = loymaxService.resetPassword(LoymaxResetPasswordRequest.builder()
                    .newPassword(confirmCodeRequestDto.getPassword())
                    .confirmCode(confirmCodeRequestDto.getCode())
                    .notifierIdentity(recoveryDto.getNotifierIdentity())
                    .build());
            User user = new User();
            String phonenNumber = loymaxService.getPhoneNumber(tokenExchangeResponse.getAccessToken());
            user.setPhoneNumber(phonenNumber);
            user.setId(recoveryDto.getUserId());
            userService.update(user);

            var encodedPassword = bCryptPasswordEncoder.encode(confirmCodeRequestDto.getPassword());
            LoymaxUser loymaxUser;
            try {
                user = userService.findByUserId(recoveryDto.getUserId());
                user.setPassword(encodedPassword);
                userService.update(user);
                loymaxUser = loymaxService.getByLocalUserId(user.getId());
                loymaxService.addTokensForUserAndDoUpdate(loymaxUser, tokenExchangeResponse);
            } catch (UserNotFoundException | ResourceNotFoundException e) {
                var loymaxData = loymaxService.getUserInfo(tokenExchangeResponse.getAccessToken());
                var anotherUser = userService
                        .createFromLoymaxData(loymaxData,
                                recoveryDto.getNotifier().getName().equals("phoneNumber") ? recoveryDto.getNotifierIdentity() : null,
                                encodedPassword, List.of(roleService.getByName(RoleNames.CLIENT.getName())));
                anotherUser.setPhoneNumber(phonenNumber);
                user = userService.saveOrUpdate(anotherUser);
                loymaxUser = new LoymaxUser();
                loymaxUser.setUserId(user.getId());
                loymaxUser.setPersonUid(loymaxData.getPersonUid());
                loymaxService.addTokensForUserAndDoUpdate(loymaxUser, tokenExchangeResponse);
                loymaxUser.setToken(tokenExchangeResponse.getAccessToken());
                loymaxService.setMobile(loymaxUser);
            }
            return authorizationService.generateUserRolesResponse(
                    userService.createDto(user));
        }
        throw new UnauthorizedException("recovery token expired");
    }
}
