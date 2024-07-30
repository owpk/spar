package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
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
@Slf4j
public class RecoveryPasswordServiceImpl implements RecoveryPasswordService {
    private final LoymaxServiceImpl loymaxService;
    private final PasswordRecoveryRequestService passwordRecoveryRequestService;
    private final UserServiceImpl userService;
    private final AuthorizationServiceImpl authorizationService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RoleServiceImpl roleService;

    /**
     * В таблице recoveryPasswordRequests создается запись о восстановлении пароля. Token в формате uuid. Время жизни токена 3 суток.
     */
    @Override
    public RecoveryTokenResponseDto recover(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        log.info("Start recovery-password with user identity: {}", recoveryPasswordRequestDto.getNotifierIdentity());

        var userDto = userService.createDto(getUser(recoveryPasswordRequestDto));
        var recoveryDto = new RecoveryTokenResponseDto();
        recoveryDto.setRecoveryToken(UUID.randomUUID().toString());

        loymaxService.recoverPassword(recoveryPasswordRequestDto);
        passwordRecoveryRequestService.createPasswordRecoveryRequestRecord(recoveryPasswordRequestDto, recoveryDto.getRecoveryToken(), userDto.getId());

        log.info("End recovery-password for user id: {} with token: {}", userDto.getId(), recoveryDto.getRecoveryToken());
        return recoveryDto;
    }

    private User getUser(RecoveryPasswordRequestDto recoveryPasswordRequestDto) {
        NotifierIdentityNames notifier = NotifierIdentityNames.fromString(recoveryPasswordRequestDto.getNotifier());

        return (notifier.equals(NotifierIdentityNames.PHONE)) ?
                userService.findByPhone(recoveryPasswordRequestDto.getNotifierIdentity()) :
                userService.findByEmail(recoveryPasswordRequestDto.getNotifierIdentity()) ;
    }

    /**
     * Получаем DTO по токену восстановления
     * Проверяем не истек ли он
     * Меняем пароль в лоймаксе ?
     * Получаем лоймакс юзера из нашей базы
     * Добавляем токены пришедшие из лоймакса
     * Получаем нашего юзера
     * Делаем из него DTO и возвращаем из метода
    */
    @Override
    public TokenDataDto recoveryConfirm(RecoveryPasswordConfirmCodeRequestDto confirmCodeRequestDto) {
        log.info("Start recover-password-confirm with recovery token: {}", confirmCodeRequestDto.getRecoveryToken());

        var recoveryDto = passwordRecoveryRequestService.getByToken(confirmCodeRequestDto.getRecoveryToken());
        if(recoveryDto.isExpired()) throw new UnauthorizedException("Recovery token has expired");

        var tokenExchangeResponse = loymaxService.resetPassword(LoymaxResetPasswordRequest.builder()
                .newPassword(confirmCodeRequestDto.getPassword())
                .confirmCode(confirmCodeRequestDto.getCode())
                .notifierIdentity(recoveryDto.getNotifierIdentity())
                .build());

        LoymaxUser loymaxUser = loymaxService.getByLocalUserId(recoveryDto.getUserId());
        loymaxService.addTokensForUserAndDoUpdate(loymaxUser, tokenExchangeResponse);

        User user = userService.findByUserId(recoveryDto.getUserId());
        log.info("End recover-password-confirm with user id {} and loymax user id: {}", loymaxUser.getUserId(), loymaxUser.getLoymaxUserId());
        return authorizationService.generateUserRolesResponse(userService.createDto(user));
    }
}
