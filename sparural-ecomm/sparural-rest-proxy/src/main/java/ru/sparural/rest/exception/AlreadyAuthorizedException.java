package ru.sparural.rest.exception;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public class AlreadyAuthorizedException extends RuntimeException {
    private final int code = 403;

    public AlreadyAuthorizedException() {
        super("You already authorized");
    }
}
