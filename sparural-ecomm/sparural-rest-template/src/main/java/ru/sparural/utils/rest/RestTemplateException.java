package ru.sparural.utils.rest;

/**
 * @author Vorobyev Vyacheslav
 */
public class RestTemplateException extends RuntimeException {
    public RestTemplateException() {
    }

    public RestTemplateException(String message) {
        super(message);
    }
}
