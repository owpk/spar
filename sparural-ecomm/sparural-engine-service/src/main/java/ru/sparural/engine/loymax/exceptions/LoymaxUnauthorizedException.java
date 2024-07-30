package ru.sparural.engine.loymax.exceptions;

import ru.sparural.engine.services.exception.UnauthorizedException;

/**
 * @author Vorobyev Vyacheslav
 */
public class LoymaxUnauthorizedException extends UnauthorizedException {

    public LoymaxUnauthorizedException(String message, int status) {
        super(message, status);
    }

    public LoymaxUnauthorizedException(String message) {
        super(message);
    }
}
