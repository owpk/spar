package ru.sparural.engine.services.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class ResourceNotFoundException extends StatusException {
    public ResourceNotFoundException(int status) {
        super(status);
    }

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String message) {
        super(message, CodeConstants.NOT_FOUND);
    }

    public ResourceNotFoundException(String message, int status) {
        super(message, status);
    }
}
