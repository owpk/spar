package ru.sparural.file.exceptions;

public class StatusCodeException extends ApplicationException {

    private final int statusCode;

    public StatusCodeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
