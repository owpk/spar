package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.RecoveryPasswordConfirmCodeRequestDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.RecoveryTokenResponseDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.kafka.exception.KafkaControllerException;

/**
 * @author Vorobyev Vyacheslav
 */
public interface RecoveryPasswordService {
    RecoveryTokenResponseDto recover(RecoveryPasswordRequestDto recoveryPasswordRequestDto) throws KafkaControllerException;

    TokenDataDto recoveryConfirm(RecoveryPasswordConfirmCodeRequestDto confirmCodeRequestDto) throws KafkaControllerException;
}
