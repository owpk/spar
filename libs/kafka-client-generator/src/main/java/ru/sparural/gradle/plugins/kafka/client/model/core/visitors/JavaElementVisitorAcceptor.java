package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

public interface JavaElementVisitorAcceptor {
    void accept(JavaElementVisitor visitor, Integer tabs);
}
