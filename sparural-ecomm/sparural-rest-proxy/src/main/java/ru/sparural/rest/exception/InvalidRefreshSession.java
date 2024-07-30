package ru.sparural.rest.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class InvalidRefreshSession extends RuntimeException {
    public InvalidRefreshSession(String message) {
        super(message);
    }
}
