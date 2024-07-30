package ru.sparural.gradle.plugins.kafka.client.core;

import ru.sparural.gradle.plugins.kafka.client.core.impl.generator.SourceCodeEntry;
import ru.sparural.gradle.plugins.kafka.client.utils.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SourceCodeWriter {
    public void write(List<SourceCodeEntry> entries, Path targetDir) {
        entries.forEach(entry -> {
            var pkg = FileUtils.replaceJavaPath(targetDir + File.separator + entry.getPackageName());
            FileUtils.createTargetDirectories(pkg);
            FileUtils.writeContent(entry.getSourceCode(), Paths.get(pkg, entry.getClassName() + ".java"));
        });
    }
}
