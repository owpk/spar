package ru.sparural.gradle.plugins.kafka.client.core;

import java.nio.file.Path;
import java.util.List;

public abstract class SourceExplorer<T> {
    protected final SourceCodeProvider provider;

    public SourceExplorer(SourceCodeProvider provider) {
        this.provider = provider;
    }

    public List<T> walkSource() {
        var source = provider.locateSourceCode();
        return walkSource(source);
    }

    protected abstract List<T> walkSource(Path path);

}