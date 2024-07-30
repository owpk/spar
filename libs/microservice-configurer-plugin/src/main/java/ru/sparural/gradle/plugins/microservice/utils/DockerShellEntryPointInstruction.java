package ru.sparural.gradle.plugins.microservice.utils;

import com.bmuschko.gradle.docker.tasks.image.Dockerfile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class DockerShellEntryPointInstruction implements Dockerfile.Instruction {
    public static final String KEYWORD = "ENTRYPOINT";
    private final String args;

    public DockerShellEntryPointInstruction( Dockerfile.EntryPointInstruction entrypoint) {
        this.args = Stream.of(StringUtils.strip(entrypoint.getText().replace("ENTRYPOINT ", ""), "[]").split(", "))
                .map(entry -> StringUtils.strip(entry, "\""))
                .collect(Collectors.joining(" "));
    }

    @Override
    public String getKeyword() {
        return KEYWORD;
    }

    @Override
    public String getText() {
        return "ENTRYPOINT exec " + args;
    }

}
