package ru.sparural.gradle.plugins.kafka.client.core;

import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;

import java.nio.file.Path;

public interface SourceCodeProvider {

    Path locateSourceCode() throws SourceLoadingException;
}
