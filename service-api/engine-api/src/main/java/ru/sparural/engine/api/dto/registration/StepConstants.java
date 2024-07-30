package ru.sparural.engine.api.dto.registration;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum StepConstants {
    BEGIN(1, "Init the start of registration"),
    LOYMAX_CONFIRM(2, "Init confirmation registration from user"),
    SET_PASSWORD(3, "Init set password"),
    SET_USER_INFO(4, "Init send user info, e.g user profile photo etc."),
    COMPLETED(5, "Registration completed");

    private final static Map<Integer, StepConstants> stepToConstant = Arrays.stream(StepConstants.values())
            .collect(Collectors.toMap(x -> x.step, Function.identity()));

    private final Integer step;
    private final String desc;

    StepConstants(int step, String desc) {
        this.step = step;
        this.desc = desc;
    }

    public static StepConstants getByStep(Integer step) {
        return Optional.ofNullable(stepToConstant.get(step))
                .orElseThrow(() -> new RuntimeException("No registration step found by key: " + step));
    }
}
