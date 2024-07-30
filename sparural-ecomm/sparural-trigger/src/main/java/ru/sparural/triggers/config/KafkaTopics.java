package ru.sparural.triggers.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaTopics {
    @Value("${sparural.kafka.request-topics.engine}")
    private String engineRequestTopicName;

    @Value("${sparural.kafka.request-topics.notification.main}")
    private String notificationRequestMainTopicName;
}
