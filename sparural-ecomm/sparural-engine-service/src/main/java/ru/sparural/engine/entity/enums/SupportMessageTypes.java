package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum SupportMessageTypes {

    text("text"),
	file("file");

    private final String val;

    SupportMessageTypes(String val) {
        this.val = val;
    }

    private final static Map<String, SupportMessageTypes> valMap = Arrays.stream(SupportMessageTypes.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static SupportMessageTypes getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
