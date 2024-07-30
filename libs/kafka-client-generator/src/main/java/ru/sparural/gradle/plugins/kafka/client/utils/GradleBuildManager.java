package ru.sparural.gradle.plugins.kafka.client.utils;

import ru.sparural.gradle.plugins.kafka.client.exceptions.SourceLoadingException;

import java.io.File;
import java.io.IOException;

public class GradleBuildManager {

    public static void build(String buildRootPath, String moduleName) {
        var isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        var gradleWrapperExecutable = "./gradlew" + (isWindows ? ".bat" : "");
        execGradleWrapperCommand(buildRootPath, gradleWrapperExecutable, "clean", moduleName);
        execGradleWrapperCommand(buildRootPath, gradleWrapperExecutable, "shadowJar", moduleName);
    }

    private static String getCommand(String command, String moduleName) {
        return moduleName != null ? (":" + moduleName + ":" + command) : command;
    }

    private static void execGradleWrapperCommand(String gradlewDir, String gradlew, String command, String moduleName) {
        try {
            var gradlewCommand = getCommand(command, moduleName);
            var pb = new ProcessBuilder(gradlew, gradlewCommand);
            pb.directory(new File(gradlewDir));
            Process p = pb.start();
            System.out.println("Building gradle project: \n\t | " + p.info());
            var status = p.waitFor();
            if (status != 0)
                throw new SourceLoadingException("\n\tCannot build gradle project\n\t\t" +
                        "ensure you have gradle shadow plugin applied in target project, \n\t\tor try to build project manually with gradle/gradlew shadowJar command");
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
