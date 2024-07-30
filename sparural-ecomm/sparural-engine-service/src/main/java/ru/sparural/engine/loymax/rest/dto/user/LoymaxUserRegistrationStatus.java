package ru.sparural.engine.loymax.rest.dto.user;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum LoymaxUserRegistrationStatus {
    NORMAL("Normal"), //  не зарегистрирован
    ANON("Anonymous"), //  анонимный
    DELETED("Deleted"), //  удален
    DEREGISTERED("Deregistered"), // отказался от участия в ПЛ
    REGISTERED("Registered"); // зарегистрирован
    private static final Map<String, LoymaxUserRegistrationStatus> valueMap = Arrays.stream(LoymaxUserRegistrationStatus.values())
            .collect(Collectors.toMap(x -> x.value, Function.identity()));

    @Getter
    private final String value;

    LoymaxUserRegistrationStatus(String value) {
        this.value = value;
    }

    public static LoymaxUserRegistrationStatus getByValue(String statusName) {
        return Optional.ofNullable(valueMap.get(statusName))
                .orElseThrow(() -> new RuntimeException("No loymax user status found: " + statusName));
    }

}
