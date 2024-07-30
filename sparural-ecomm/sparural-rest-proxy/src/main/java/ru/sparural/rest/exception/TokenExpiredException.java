package ru.sparural.rest.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}
