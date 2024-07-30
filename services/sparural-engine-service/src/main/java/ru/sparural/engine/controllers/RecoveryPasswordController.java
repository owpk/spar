package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import ru.sparural.engine.api.dto.RecoveryPasswordConfirmCodeRequestDto;
import ru.sparural.engine.api.dto.RecoveryPasswordRequestDto;
import ru.sparural.engine.api.dto.RecoveryTokenResponseDto;
import ru.sparural.engine.api.dto.user.TokenDataDto;
import ru.sparural.engine.services.RecoveryPasswordService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.exception.KafkaControllerException;

/**
 * @author Vorobyev Vyacheslav
 */
@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
public class RecoveryPasswordController {

    private final RecoveryPasswordService recoveryPasswordService;

    @KafkaSparuralMapping("recovery-password")
    public RecoveryTokenResponseDto recoveryPassword(@Payload RecoveryPasswordRequestDto recoveryPasswordRequestDto) throws KafkaControllerException {
        return recoveryPasswordService.recover(recoveryPasswordRequestDto);
    }

    @KafkaSparuralMapping("recovery-password-confirm")
    public TokenDataDto recoveryPasswordConfirm(@Payload RecoveryPasswordConfirmCodeRequestDto recoveryPasswordRequestDto) throws KafkaControllerException {
        return recoveryPasswordService.recoveryConfirm(recoveryPasswordRequestDto);
    }

}
