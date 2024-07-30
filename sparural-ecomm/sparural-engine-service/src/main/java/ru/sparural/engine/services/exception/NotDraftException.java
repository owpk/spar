package ru.sparural.engine.services.exception;

public class NotDraftException extends StatusException {

    public NotDraftException(int status) {
        super(status);
    }

    public NotDraftException() {
    }

    public NotDraftException(String message) {
        super(message, CodeConstants.FORBIDDEN);
    }

    public NotDraftException(String message, int status) {
        super(message, status);
    }
}
