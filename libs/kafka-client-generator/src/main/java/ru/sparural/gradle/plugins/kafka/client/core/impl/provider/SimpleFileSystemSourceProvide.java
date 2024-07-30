package ru.sparural.gradle.plugins.kafka.client.core.impl.provider;

import ru.sparural.gradle.plugins.kafka.client.core.GradleBuildable;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleFileSystemSourceProvide
        extends AbstractSourceLoader implements GradleBuildable {

    private final Path source;

    public SimpleFileSystemSourceProvide(String path) {
        this.source = Paths.get(path);
    }

    @Override
    protected Path provideSource() {
        return source;
    }
}
