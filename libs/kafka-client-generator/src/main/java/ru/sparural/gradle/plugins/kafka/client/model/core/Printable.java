package ru.sparural.gradle.plugins.kafka.client.model.core;

import ru.sparural.gradle.plugins.kafka.client.model.core.visitors.JavaElementVisitorAcceptor;

import java.util.List;

public interface Printable extends JavaElementVisitorAcceptor {
    String getBegin();

    List<Printable> getBody();

    String getEnd();
}
