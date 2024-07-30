package ru.sparural.gradle.plugins.kafka.client.builder;

import java.util.List;

public interface Printable extends JavaElementVisitorAcceptor {
    String getBegin();
    List<Printable> getBody();
    String getEnd();
}
