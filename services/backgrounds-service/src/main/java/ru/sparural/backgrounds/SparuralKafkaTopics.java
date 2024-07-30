package ru.sparural.backgrounds;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@Getter
public class SparuralKafkaTopics {

    @Value("${sparural.kafka.request-topics.engine}")
    private String engineTopicName;
}
