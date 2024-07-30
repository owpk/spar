package ru.sparural.engine.services.exception;

/**
 * @author Vorobyev Vyacheslav
 */
public class UserNotFoundException extends StatusException {
    public UserNotFoundException(String userID, int status) {
        super("user not found: " + userID, status);
    }

    public UserNotFoundException(String userID) {
        this(userID, CodeConstants.FORBIDDEN);
    }
}
