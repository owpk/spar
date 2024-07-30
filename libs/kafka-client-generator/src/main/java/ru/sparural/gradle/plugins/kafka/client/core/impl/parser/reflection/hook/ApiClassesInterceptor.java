package ru.sparural.gradle.plugins.kafka.client.core.impl.parser.reflection.hook;

import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.SourceCodeEntry;

import java.util.List;

public interface ApiClassesInterceptor {
    void acceptClass(Class<?> cl);

    List<SourceCodeEntry> getEntriesToWrite();
}
