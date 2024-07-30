package ru.sparural.gradle.plugins.kafka.client.model.core.visitors;

import ru.sparural.gradle.plugins.kafka.client.model.core.JavaCodeElement;

public interface JavaElementVisitor {
    void visit(JavaCodeElement element, Integer tabs);
}
