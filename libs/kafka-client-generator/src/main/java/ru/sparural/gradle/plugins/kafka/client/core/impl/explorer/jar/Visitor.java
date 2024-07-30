package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar;

import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.List;

public interface Visitor extends FileVisitor<Path> {
    List<String> getClassNames();
}
