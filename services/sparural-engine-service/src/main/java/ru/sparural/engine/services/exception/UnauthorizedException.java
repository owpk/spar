package ru.sparural.engine.services.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
public class UnauthorizedException extends StatusException {
    public UnauthorizedException(String message, int status) {
        super(message, status);
    }

    public UnauthorizedException(String message) {
        super(message, CodeConstants.UNAUTHORIZED);
    }
}
