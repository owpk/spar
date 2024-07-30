package ru.sparural.rest.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sparural.engine.api.dto.defaults.MessageDto;
import ru.sparural.kafka.utils.SparuralKafkaBadKafkaResponseException;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.exception.AlreadyAuthorizedException;
import ru.sparural.rest.exception.InvalidRefreshSession;
import ru.sparural.rest.exception.RestRequestException;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {SparuralKafkaBadKafkaResponseException.class})
    protected ResponseEntity<?> handleRestToKafkaExceptions(
            SparuralKafkaBadKafkaResponseException ex) {
        log.error("Rest to kafka exception. Correlation id: {}",
                new String(ex.getKafkaResponseMessage().getCorrelationId()));
        return ResponseEntity.status(
                        ex.getKafkaResponseMessage().getStatus().getCode())
                .body(ex.getKafkaResponseMessage().getPayload());
    }

    @ExceptionHandler(value = InvalidRefreshSession.class)
    protected ResponseEntity<?> handleRefreshSessionExceptions(InvalidRefreshSession invalidRefreshSession) {
        log.error("Invalid refresh token request.", invalidRefreshSession);
        return ResponseEntity.status(403).body(invalidRefreshSession.getLocalizedMessage());
    }

    @ExceptionHandler(value = RestRequestException.class)
    protected ResponseEntity<?> handleRestRequestExceptions(RestRequestException restRequestException) {
        log.error("Invalid rest request.", restRequestException);
        var dataResponse = new UnwrappedGenericDto<MessageDto>();
        var msg = new MessageDto(restRequestException.getLocalizedMessage());
        dataResponse.setData(msg);
        dataResponse.setSuccess(false);
        dataResponse.setCode(restRequestException.getStatus());
        return ResponseEntity.status(restRequestException.getStatus()).body(dataResponse);
    }

    @ExceptionHandler(value = AlreadyAuthorizedException.class)
    protected ResponseEntity<?> handleAlreadyAuthorizedExceptions(AlreadyAuthorizedException alreadyAuthorizedException) {
        log.error("User already authorized exception.", alreadyAuthorizedException);
        var dataResponse = new UnwrappedGenericDto<MessageDto>();
        var msg = new MessageDto(alreadyAuthorizedException.getLocalizedMessage());
        dataResponse.setData(msg);
        dataResponse.setSuccess(false);
        dataResponse.setCode(alreadyAuthorizedException.getCode());
        return ResponseEntity.status(alreadyAuthorizedException.getCode()).body(dataResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Validation exception.", ex);
        var dataResponse = new UnwrappedGenericDto<MessageDto>();
        var msg = new MessageDto(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
        dataResponse.setData(msg);
        dataResponse.setSuccess(false);
        return ResponseEntity.status(422).body(dataResponse);
    }

}