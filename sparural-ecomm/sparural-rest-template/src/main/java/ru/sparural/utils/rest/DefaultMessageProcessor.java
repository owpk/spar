package ru.sparural.utils.rest;

/**
 * @author Vorobyev Vyacheslav
 */
public abstract class DefaultMessageProcessor implements MessageProcessor {

    @Override
    public void onFailure(RestResponse response) throws RestTemplateException {
        throw new RestTemplateException(response.toString());
    }
}