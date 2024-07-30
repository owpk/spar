package ru.sparural.gradle.plugins.kafka.client.utils;

import org.eclipse.jgit.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class KafkaClientFileUtils {

    public static void writeJavaClassToFile(String destinationPackagePath, String className, String content) throws IOException {
        var codeFilePathString = destinationPackagePath + File.separator + className + ".java";
        var codeFilePath = Paths.get(codeFilePathString);
        if (!Files.exists(codeFilePath)) {
            Files.write(Files.createFile(codeFilePath),
                    content.getBytes(StandardCharsets.UTF_8));
        }
    }


    public static void cleanDestination(Path destination) throws IOException {
        if (Files.exists(destination)) {
            FileUtils.delete(destination.toFile(), FileUtils.RECURSIVE);
        }
    }

    public static Path defineTempDir() {
        var tmpDir = Paths.get(System.getProperty("java.io.tmpdir"), "spar_kafka_client_api");
        if (Files.exists(tmpDir)) {
            try {
                KafkaClientFileUtils.cleanDestination(tmpDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return tmpDir;
    }
}
