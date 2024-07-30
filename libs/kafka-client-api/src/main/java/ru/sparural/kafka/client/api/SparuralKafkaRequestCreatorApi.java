package ru.sparural.kafka.client.api;

import java.util.Map;

public interface SparuralKafkaRequestCreatorApi {
    KafkaResponseMessage send(String topic, String action);

    KafkaResponseMessage send(String topic, String action,
                              Object body);

    KafkaResponseMessage send(Map<String, Object> params, String topic,
                              String action, Object
                                      body);

    KafkaRequestInfo sendAsync(String topic, String action);

    KafkaRequestInfo sendAsync(String topic, String action, Object body);

    KafkaRequestInfo sendAsync(Map<String, Object> params, String topic, String action, Object
            body);

    <R> R sendForEntity(String topic, String action)
            throws SparuralKafkaBadKafkaResponseException;

    <R> R sendForEntity(String topic, String action, Object entity)
            throws SparuralKafkaBadKafkaResponseException;

    <R> R sendForEntity(Map<String, Object> params, String topic,
                        String action, Object entity)
            throws SparuralKafkaBadKafkaResponseException;

    <R> R sendForEntity(String topic, String action,
                        KafkaResponseHandler kafkaResponseHandler)
            throws SparuralKafkaBadKafkaResponseException;

    <R> R sendForEntity(String topic, String action, Object entity,
                        KafkaResponseHandler kafkaResponseHandler)
            throws SparuralKafkaBadKafkaResponseException;

    <R> R sendForEntity(Map<String, Object> params, String topic,
                        String action, Object entity,
                        KafkaResponseHandler kafkaResponseHandler)
            throws SparuralKafkaBadKafkaResponseException;

    SparuralKafkaRequestBuilder createRequestBuilder();
}
