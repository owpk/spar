package ru.sparural.gradle.plugins.kafka.client.model.core;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum Lang {
    STRING(String.class.getSimpleName()),
    LONG(Long.class.getSimpleName()),
    INTEGER(Integer.class.getSimpleName()),
    DOUBLE(Double.class.getSimpleName()),
    FLOAT(Float.class.getSimpleName()),
    BYTE(Byte.class.getSimpleName()),
    BOOLEAN(Long.class.getSimpleName());
    private static final Set<String> NAMES = Arrays.stream(Lang.values())
            .map(Lang::getSimpleName).collect(Collectors.toSet());
    @Getter
    private final String simpleName;

    Lang(String simpleName) {
        this.simpleName = simpleName;
    }

    public static boolean containsAny(String... names) {
        return containsAny(Arrays.asList(names));
    }

    public static boolean containsAny(List<String> names) {
        return names.stream().anyMatch(NAMES::contains);
    }
}
