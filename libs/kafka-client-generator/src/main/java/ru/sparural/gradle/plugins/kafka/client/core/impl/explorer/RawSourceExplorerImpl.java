package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer;

import ru.sparural.gradle.plugins.kafka.client.core.SourceCodeProvider;
import ru.sparural.gradle.plugins.kafka.client.core.SourceExplorer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

public class RawSourceExplorerImpl extends SourceExplorer<String> {

    public RawSourceExplorerImpl(SourceCodeProvider provider) {
        super(provider);
    }

    @Override
    protected List<String> walkSource(Path source) {
        var result = new ArrayList<String>();
        var visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) throws IOException {
                if (!file.toString().endsWith(".java"))
                    return super.visitFile(file, attrs);
                var data = Files.readString(file);
                result.add(data);
                return super.visitFile(file, attrs);
            }
        };
        try {
            Files.walkFileTree(source, visitor);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
