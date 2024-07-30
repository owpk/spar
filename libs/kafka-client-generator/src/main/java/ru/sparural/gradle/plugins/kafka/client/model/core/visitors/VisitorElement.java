package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

public interface VisitorElement {
    void accept(JavaClassVisitor visitor);
}
