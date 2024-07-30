/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.sparural.file.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.sparural.engine.api.dto.defaults.ExceptionResponseDto;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.exceptions.NotFoundException;
import ru.sparural.file.exceptions.StatusCodeException;
import ru.sparural.file.exceptions.TimeoutException;
import ru.sparural.file.security.UserPrincipal;
import ru.sparural.kafka.model.KafkaResponseMessage;

import java.util.List;
import java.util.Optional;

/**
 * @author aeysner
 */
public class FileServerUtils {

    public static <T> T checkEngineResponse(KafkaResponseMessage response, Class<T> type) {
        switch (response.getStatus()) {
            case NOT_FOUND:
                throw new NotFoundException(extractMessageFromResponse(response));

            case SUCCESS:
                return (T) response.getPayload();

            case TIMEOUT:
                throw new TimeoutException();

            case STATUS_CODE:
                throw new StatusCodeException(extractMessageFromResponse(response), response.getStatus().getCode());

            default:
                throw new ApplicationException("Unexceptied error from engine server");
        }

    }

    public static <T> List<T> checkEngineResponseList(KafkaResponseMessage response, Class<T> type) {
        switch (response.getStatus()) {
            case NOT_FOUND:
                throw new NotFoundException(extractMessageFromResponse(response));

            case SUCCESS:
                return (List<T>) response.getPayload();

            case TIMEOUT:
                throw new TimeoutException();

            case STATUS_CODE:
                throw new StatusCodeException(extractMessageFromResponse(response), response.getStatus().getCode());

            default:
                throw new ApplicationException("Unexceptied error from engine server");
        }

    }

    public static void checkEngineResponseVoid(KafkaResponseMessage response) {
        switch (response.getStatus()) {
            case NOT_FOUND:
                throw new NotFoundException(extractMessageFromResponse(response));

            case SUCCESS:
                break;

            case TIMEOUT:
                throw new TimeoutException();

            case STATUS_CODE:
                throw new StatusCodeException(extractMessageFromResponse(response), response.getStatus().getCode());

            default:
                throw new ApplicationException("Unexceptied error from engine server");
        }

    }

    private static String extractMessageFromResponse(KafkaResponseMessage response) {
        ExceptionResponseDto ex = (ExceptionResponseDto) response.getPayload();
        return ex.getMessage().toString();
    }

    public static UserPrincipal getUserPrincipal() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        } else {
            return null;
        }
    }

    public static Long getUserId() {
        return Optional.ofNullable(getUserPrincipal())
                .map(UserPrincipal::getUserId)
                .orElse(null);
    }
}
