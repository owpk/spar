package ru.sparural.kafka.starter;

import ru.sparural.kafka.client.api.KafkaProducer;

import java.util.UUID;

public class RequestCreatorImpl implements KafkaProducer {

    @Override
    public Object send(String msg, String topic) {
        System.out.printf("KAFKA REQUEST %s SENT MESSAGE :: '%s' :: TO TOPIC %s%n", UUID.randomUUID(), msg, topic);
        return "OK";
    }
}
