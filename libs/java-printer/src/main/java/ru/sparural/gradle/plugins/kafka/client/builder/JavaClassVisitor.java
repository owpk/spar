package ru.sparural.gradle.plugins.kafka.client.builder;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

public interface JavaClassVisitor {
    void visit(JavaClass javaClass);
}
