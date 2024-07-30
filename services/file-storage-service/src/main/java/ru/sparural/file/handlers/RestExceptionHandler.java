package ru.sparural.file.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.sparural.file.dto.FileServerResponse;
import ru.sparural.file.exceptions.NotFoundException;
import ru.sparural.file.exceptions.StatusCodeException;
import ru.sparural.file.exceptions.TimeoutException;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<FileServerResponse> handleNotFoundException(NotFoundException ex) {
        FileServerResponse<String> response = new FileServerResponse<>();
        response.setData(ex.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StatusCodeException.class)
    public ResponseEntity<FileServerResponse> handleStatusCodeException(StatusCodeException ex) {
        FileServerResponse<String> response = new FileServerResponse<>();
        response.setData(ex.getMessage());
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatusCode()));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<FileServerResponse> handleTimeoutException(TimeoutException ex) {
        FileServerResponse<String> response = new FileServerResponse<>();
        response.setData("Engine Server timeout error");
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<FileServerResponse> handleApplicationException(RuntimeException ex) {
        log.error("Unexcepted exception", ex);
        FileServerResponse<String> response = new FileServerResponse<>();
        response.setData("Unexcepted exception. Please contact administrator");
        response.setSuccess(false);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
