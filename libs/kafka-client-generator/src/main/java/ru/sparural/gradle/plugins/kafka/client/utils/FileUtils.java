package ru.sparural.gradle.plugins.kafka.client.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static String replaceJavaPath(String pkgName) {
        return pkgName.replaceAll("\\.", File.separator);
    }

    public static void createTargetDirectories(String path) {
        try {
            var packagePath = Paths.get(path);
            if (!Files.exists(packagePath))
                Files.createDirectories(packagePath);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeContent(String content, Path targetPath) {
        try {
            var filePath = Paths.get(targetPath.toString());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                System.out.println("generating java file: " + filePath);
                Files.writeString(filePath, content);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
