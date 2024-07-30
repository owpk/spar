package ru.sparural.kafka.client.api;

/**
 * @author Vorobyev Vyacheslav
 */
public interface KafkaResponseHandler {
    <R> R handleResponse(KafkaResponseMessage response);
}