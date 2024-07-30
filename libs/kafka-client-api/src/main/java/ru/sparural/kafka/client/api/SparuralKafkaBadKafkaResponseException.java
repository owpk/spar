package ru.sparural.kafka.client.api;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public class SparuralKafkaBadKafkaResponseException extends RuntimeException {

    private final KafkaResponseMessage kafkaResponseMessage;

    public SparuralKafkaBadKafkaResponseException(KafkaResponseMessage kafkaResponseMessage) {
        this.kafkaResponseMessage = kafkaResponseMessage;
    }
}
