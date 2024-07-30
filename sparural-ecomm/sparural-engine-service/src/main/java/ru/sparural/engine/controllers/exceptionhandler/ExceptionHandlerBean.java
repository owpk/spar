package ru.sparural.engine.controllers.exceptionhandler;

import ru.sparural.engine.api.dto.defaults.ExceptionResponseDto;
import ru.sparural.engine.api.dto.defaults.MessageDto;
import ru.sparural.engine.loymax.exceptions.LoymaxException;
import ru.sparural.engine.services.exception.*;
import ru.sparural.kafka.annotation.ExceptionHandler;
import ru.sparural.kafka.annotation.KafkaSparuralExceptionHandler;
import ru.sparural.kafka.consumer.KafkaResponseStatus;
import ru.sparural.kafka.exception.KafkaControllerException;
import ru.sparural.kafka.model.KafkaRequestMessage;
import ru.sparural.kafka.model.KafkaResponseMessage;

/**
 * @author Vorobyev Vyacheslav
 */
@KafkaSparuralExceptionHandler
public class ExceptionHandlerBean {

    @ExceptionHandler(ValidationException.class)
    public KafkaResponseMessage handleValidationException(ValidationException ex, KafkaRequestMessage requestMessage) {
        return createDefaultResponse(ex, requestMessage);
    }

    @ExceptionHandler(RegistrationStepException.class)
    public KafkaResponseMessage handleRegistrationErrors(RegistrationStepException ex, KafkaRequestMessage requestMessage) {
        return createDefaultResponse(ex, requestMessage);
    }

    @ExceptionHandler(KafkaControllerException.class)
    public KafkaResponseMessage handleKafkaControllerExceptions(KafkaControllerException ex, KafkaRequestMessage requestMessage) {
        var resp = createDefaultResponseWithoutBody(requestMessage);
        resp.setPayload(buildExceptionDto(ex, false));
        resp.setStatus(KafkaResponseStatus.STATUS_CODE.status(ex.getStatusCode()));
        return resp;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public KafkaResponseMessage handleUserNotFound(UserNotFoundException ex, KafkaRequestMessage requestMessage) {
        return createDefaultResponse(ex, requestMessage);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public KafkaResponseMessage handleResourceNotFound(ResourceNotFoundException ex, KafkaRequestMessage requestMessage) {
        return createDefaultResponse(ex, requestMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public KafkaResponseMessage handleUnauthorizedException(UnauthorizedException ex, KafkaRequestMessage requestMessage) {
        return createDefaultResponse(ex, requestMessage);
    }

    @ExceptionHandler(LoymaxException.class)
    public KafkaResponseMessage handleLoymaxExceptions(LoymaxException ex, KafkaRequestMessage requestMessage) {
        return buildKafkaResponse(requestMessage, buildExceptionDto(ex, false), 503);
    }

    @ExceptionHandler(NotDraftException.class)
    public KafkaResponseMessage handleNotDraftException(NotDraftException ex, KafkaRequestMessage requestMessage) {
        return buildKafkaResponse(requestMessage, buildExceptionDto(ex, false), 403);
    }

    private KafkaResponseMessage createDefaultResponse(StatusException ex, KafkaRequestMessage requestMessage) {
        return buildKafkaResponse(requestMessage, buildExceptionDto(ex, false), ex.getStatus());
    }

    private KafkaResponseMessage createDefaultResponseWithoutBody(KafkaRequestMessage requestMessage) {
        var response = new KafkaResponseMessage();
        response.setCorrelationId(requestMessage.getCorrelationId());
        response.setReplyTopic(requestMessage.getReplyTopic());
        return response;
    }

    private ExceptionResponseDto<MessageDto> buildExceptionDto(Exception e, Boolean success) {
        return ExceptionResponseDto.<MessageDto>builder()
                .message(new MessageDto(e.getMessage()))
                .success(success)
                .build();
    }

    private KafkaResponseMessage buildKafkaResponse(KafkaRequestMessage requestMessage,
                                                    ExceptionResponseDto<MessageDto> body, Integer code) {
        var response = new KafkaResponseMessage();
        response.setCorrelationId(requestMessage.getCorrelationId());
        response.setReplyTopic(requestMessage.getReplyTopic());
        response.setPayload(body);
        response.setStatus(KafkaResponseStatus.STATUS_CODE.status(code));
        return response;
    }
}