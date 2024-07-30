package ru.sparural.gradle.plugins.kafka.client.core.impl.provider;

import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractSourceLoader implements SourceCodeProvider {

    @Override
    public Path locateSourceCode() throws SourceLoadingException {
        return checkPath(provideSource());
    }

    protected abstract Path provideSource();

    protected Path checkPath(Path path) {
        // TODO files utils
        if (path == null || !Files.exists(path)) {
            // TODO check permissions etc
            throw new SourceLoadingException("Source code path not exists");
        }
        return path;
    }
}
