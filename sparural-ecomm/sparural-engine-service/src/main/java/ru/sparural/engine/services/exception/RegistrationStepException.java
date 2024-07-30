package ru.sparural.engine.services.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class RegistrationStepException extends StatusException {
    public RegistrationStepException(int step, int code) {
        super(String.valueOf(step), code);
    }

    public RegistrationStepException(String msg, int code) {
        super(msg, code);
    }

    public RegistrationStepException(int step) {
        this(step, 423);
    }

}
