package ru.sparural.gradle.plugins.kafka.client.builder;

public interface VisitorElement {
    void accept(JavaClassVisitor visitor);
}
