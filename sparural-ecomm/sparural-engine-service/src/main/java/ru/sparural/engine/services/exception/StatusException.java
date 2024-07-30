package ru.sparural.engine.services.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
public class StatusException extends RuntimeException {
    private int status;

    public StatusException(int status) {
        this.status = status;
    }

    public StatusException() {
        status = CodeConstants.PROCESS_EXECUTION_EXCEPTION;
    }

    public StatusException(String message) {
        super(message);
    }

    public StatusException(String message, int status) {
        super(message);
        this.status = status;
    }
}