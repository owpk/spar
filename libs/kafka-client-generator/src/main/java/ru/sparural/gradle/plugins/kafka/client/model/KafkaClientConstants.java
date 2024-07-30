package ru.sparural.gradle.plugins.kafka.client.model;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;
import ru.sparural.gradle.plugins.kafka.client.model.core.JavaVariable;
import ru.sparural.gradle.plugins.kafka.client.utils.Constants;

public class KafkaClientConstants {

    public static final JavaVariable kafkaRCVariable = JavaVariable.builder()
            .type(
                    JavaClass.builder()
                            .packageName(Constants.KAFKA_API_CLIENT_PACKAGE)
                            .typeName("SparuralKafkaRequestBuilder")
                            .build()
            )
            .variableName("kafkaRequestBuilder")
            .isFinal(true)
            .build();

    public static final JavaVariable reqTopicName = JavaVariable.builder()
            .type(
                    JavaClass.builder()
                            .typeName("String")
                            .build()
            )
            .variableName("requestTopicName")
            .isFinal(true)
            .build();

    public static final JavaClass complFuture = JavaClass.builder()
            .packageName("java.util.concurrent")
            .typeName("CompletableFuture")
            .build();

    public static final JavaClass kafkaStarterApiIFace = JavaClass.builder()
            .typeName("KafkaApiClient")
            .packageName(Constants.KAFKA_API_CLIENT_PACKAGE)
            .build();

    public static final JavaClass kafkaRequestInfo = JavaClass
            .builder()
            .typeName("KafkaRequestInfo")
            .packageName(Constants.KAFKA_API_CLIENT_PACKAGE)
            .build();
}
