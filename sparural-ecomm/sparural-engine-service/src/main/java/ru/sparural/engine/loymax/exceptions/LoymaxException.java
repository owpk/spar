package ru.sparural.engine.loymax.exceptions;

/**
 * @author Vorobyev Vyacheslav
 */
public class LoymaxException extends RuntimeException {

    public LoymaxException(String message) {
        super(message);
    }

    public LoymaxException() {
        super("No response from loymax");
    }

}