/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ru.sparural.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaTopics {
    @Value("${sparural.kafka.request-topics.file}")
    private String fileRequestTopicName;

    @Value("${sparural.kafka.request-topics.triggers}")
    private String triggersRequestTopicName;
}
