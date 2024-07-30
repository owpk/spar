package ru.sparural.utils.rest;

/**
 * @author Vorobyev Vyacheslav
 */
public interface MessageProcessor {
    void onSuccess(RestResponse response) throws RestTemplateException;

    void onFailure(RestResponse response) throws RestTemplateException;
}
