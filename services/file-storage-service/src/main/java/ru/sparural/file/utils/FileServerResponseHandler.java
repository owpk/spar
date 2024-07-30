package ru.sparural.file.utils;

import ru.sparural.engine.api.dto.defaults.ExceptionResponseDto;
import ru.sparural.file.exceptions.ApplicationException;
import ru.sparural.file.exceptions.NotFoundException;
import ru.sparural.file.exceptions.StatusCodeException;
import ru.sparural.file.exceptions.TimeoutException;
import ru.sparural.kafka.model.KafkaResponseMessage;
import ru.sparural.kafka.utils.KafkaResponseHandler;

/**
 * @author Vorobyev Vyacheslav
 */
public class FileServerResponseHandler implements KafkaResponseHandler {
    private static FileServerResponseHandler fileServerResponseHandler;

    public static KafkaResponseHandler getInstance() {
        if (fileServerResponseHandler == null) {
            fileServerResponseHandler = new FileServerResponseHandler();
        }
        return fileServerResponseHandler;
    }

    @Override
    public <R> R handleResponse(KafkaResponseMessage response) {
        switch (response.getStatus()) {
            case NOT_FOUND:
                throw new NotFoundException(extractMessageFromResponse(response));

            case SUCCESS:
                return response.getPayload() == null ? null : (R) response.getPayload();

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
}
