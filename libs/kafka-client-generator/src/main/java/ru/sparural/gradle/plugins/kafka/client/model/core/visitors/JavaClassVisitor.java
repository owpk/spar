package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaClass;

public interface JavaClassVisitor {
    void visit(JavaClass javaClass);
}
