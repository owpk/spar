package ru.sparural.kafka.client.api;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
public class KafkaRequestInfo {
    private final String correlationId;
    private final String action;
    private final String topic;
    private final CompletableFuture<KafkaResponseMessage> future;
    private final long createdAt;
    private final long expireAt;

    public KafkaRequestInfo(String correlationId,
                            CompletableFuture<KafkaResponseMessage> future,
                            int timeout,
                            String action,
                            String topic) {
        this.correlationId = correlationId;
        this.future = future;
        this.createdAt = System.currentTimeMillis();
        this.expireAt = this.createdAt + timeout * 1000L;
        this.action = action;
        this.topic = topic;
    }

}
