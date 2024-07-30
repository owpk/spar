package ru.sparural.engine.services.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class ServiceException extends StatusException {

    public ServiceException() {
        super(CodeConstants.PROCESS_EXECUTION_EXCEPTION);
    }

    public ServiceException(String message) {
        super(message, CodeConstants.PROCESS_EXECUTION_EXCEPTION);
    }

    public ServiceException(String message, int status) {
        super(message, status);
    }
}
