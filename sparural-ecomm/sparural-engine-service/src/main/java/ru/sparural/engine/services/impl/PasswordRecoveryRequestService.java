package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.entity.RecoveryPassword;
import ru.sparural.engine.repositories.impl.RecoveryPasswordRepositoryImpl;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.engine.services.exception.ValidationException;

import java.util.concurrent.TimeUnit;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class PasswordRecoveryRequestService {
    private final RecoveryPasswordRepositoryImpl recoveryPasswordRepository;

    public void createPasswordRecoveryRequestRecord(RecoveryPasswordRequestDto recoveryPasswordRequestDto,
                                                    String uuid, Long userId) {
        // seconds
        Long expirationTime = TimeUnit.DAYS.toSeconds(3) +
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        RecoveryPassword data = new RecoveryPassword();
        data.setExpired(expirationTime);
        data.setNotifier(recoveryPasswordRepository.getConfirmCodeNotifierByName(recoveryPasswordRequestDto.getNotifier())
                .orElseThrow(ResourceNotFoundException::new));
        data.setNotifierIdentity(recoveryPasswordRequestDto.getNotifierIdentity());
        data.setUserId(userId);
        data.setToken(uuid);
        recoveryPasswordRepository.create(data);
    }

    public RecoveryPassword getByToken(String uuid) {
        RecoveryPassword result = recoveryPasswordRepository.getByToken(uuid).orElseThrow(
                () -> new ValidationException("No such recovery token exists"));
        result.setNotifier(recoveryPasswordRepository.getNotifier(result.getNotifierId()).orElseThrow(
                () -> new ValidationException("No such notifier exists")));
        return result;
    }
}