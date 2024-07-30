package ru.sparural.engine.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public enum SupportChatMessageType {
    TEXT("text"),
    FILE("file");

    private static final Map<SupportChatMessageType, String> typeStringMap =
            Arrays.stream(SupportChatMessageType.values())
                    .collect(Collectors.toMap(x -> x, x -> x.type));

    private static final Map<String,SupportChatMessageType > stringTypeMap =
            Arrays.stream(SupportChatMessageType.values())
                    .collect(Collectors.toMap(x -> x.type, x -> x));

    private final String type;

    SupportChatMessageType(String type) {
        this.type = type;
    }

    public static String of(SupportChatMessageType type) {
        if (!typeStringMap.containsKey(type))
            throw new RuntimeException(
                    "No support chat message type exists: " + type +
                            ". Available message type: " + typeStringMap.values());
        return typeStringMap.get(type);
    }

    public static SupportChatMessageType of(String type) {
        if (!stringTypeMap.containsKey(type))
            throw new RuntimeException(
                    "No support chat message type exists: " + type +
                            ". Available message type: " + stringTypeMap.values());
        return stringTypeMap.get(type);
    }
}
