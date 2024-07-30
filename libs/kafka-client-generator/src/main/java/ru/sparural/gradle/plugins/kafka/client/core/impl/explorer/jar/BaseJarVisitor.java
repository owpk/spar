package ru.sparural.gradle.plugins.kafka.client.core.impl.explorer.jar;

import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class BaseJarVisitor extends SimpleFileVisitor<Path> implements Visitor {
    private static final Logger LOGGER = Logging.getLogger(BaseJarVisitor.class);

    protected List<String> classNames;

    public BaseJarVisitor() {
        classNames = new LinkedList<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        String path = file.toString();
        if (checkIfClass(path))
            visitClassFile(file, substringClassPath());
        return FileVisitResult.CONTINUE;
    }

    protected void visitClassFile(Path javaClass, Function<String, String> function) {
        LOGGER.info("Visiting class file: " + javaClass.getFileName());
        String classPath = function.apply(javaClass.toString());
        classNames.add(classPath);
    }

    protected boolean checkIfClass(String file) {
        return !file.startsWith("META-INF")
                && !file.startsWith("BOOT-INF")
                && file.endsWith(".class")
                && !file.contains("$");
    }

    protected Function<String, String> substringClassPath() {
        return x -> {
            String name = x.substring(1, x.lastIndexOf("."));
            return name.replaceAll("/", ".");
        };
    }

    @Override
    public List<String> getClassNames() {
        return classNames;
    }
}
