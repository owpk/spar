package ru.sparural.gradle.plugins.kafka.client.builder;

public interface JavaElementVisitorAcceptor {
    void accept(JavaElementVisitor visitor, Integer tabs);
}
