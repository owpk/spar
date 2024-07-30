package ru.sparural.file.dto;

import ru.sparural.file.dto.sourceparams.FileSourceRemoteParams;
import ru.sparural.file.dto.sourceparams.FileSourceRequestParams;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum FileSourceType {

    REQUEST("request", FileSourceRequestParams.class),
    REMOTE("remote", FileSourceRemoteParams.class);

    private static final Map<String, FileSourceType> types = Stream.of(FileSourceType.values()).collect(Collectors.toUnmodifiableMap(FileSourceType::getType, v -> v));
    private final String type;
    private final Class<? extends FileSourceParameters> classType;

    private FileSourceType(String type, Class<? extends FileSourceParameters> classType) {
        this.type = type;
        this.classType = classType;
    }

    public static FileSourceType of(String type) {
        return types.get(type);
    }

    public String getType() {
        return type;
    }

    public Class<? extends FileSourceParameters> getClassType() {
        return classType;
    }
}
