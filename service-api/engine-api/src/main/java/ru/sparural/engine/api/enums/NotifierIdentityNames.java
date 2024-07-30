package ru.sparural.engine.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@RequiredArgsConstructor
public enum NotifierIdentityNames {
    PHONE("phoneNumber"),
    EMAIL("email");

    private final String value;

    private static final Map<String, NotifierIdentityNames> BY_VALUES_MAP = Stream.of(NotifierIdentityNames.values())
            .collect(Collectors.toMap(NotifierIdentityNames::getValue, v -> v));

    public static NotifierIdentityNames fromString(String value) {
        return Optional
                .ofNullable(BY_VALUES_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException("Invalid NotifierIdentityNames value: " + value));
    }
}
