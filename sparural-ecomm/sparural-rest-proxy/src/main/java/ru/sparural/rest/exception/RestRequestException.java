package ru.sparural.rest.exception;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public class RestRequestException extends RuntimeException {
    private int status;

    public RestRequestException() {
        status = 500;
    }

    public RestRequestException(String message) {
        super(message);
        status = 500;
    }

    public RestRequestException(String message, int status) {
        super(message);
        this.status = status;
    }
}
