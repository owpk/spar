package ru.sparural.rest.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class RequestException extends Throwable {

    ResponseEntity<?> responseEntity;

    public RequestException(ResponseEntity<?> entity) {
        responseEntity = entity;
    }

}
