package ru.sparural.kafka.client.api;

public interface KafkaProducer {
    <R> R send(String msg, String topic);
}
