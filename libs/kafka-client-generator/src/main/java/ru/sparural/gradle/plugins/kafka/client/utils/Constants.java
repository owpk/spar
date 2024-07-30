package ru.sparural.gradle.plugins.kafka.client.utils;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

public class Constants {

    public static final String KAFKA_API_CLIENT_PACKAGE = "ru.sparural.kafka.client.api";
    public static final String PAYLOAD = "@Payload";
    public static final String REQUEST_PARAM = "@RequestParam";
    public static final JavaClass VOID_TYPE = JavaClass.builder().typeName("Void").build();

}
