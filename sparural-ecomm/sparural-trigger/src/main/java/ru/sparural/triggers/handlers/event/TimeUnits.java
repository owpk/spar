package ru.sparural.triggers.handlers.event;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum TimeUnits {
    MINUTES("m"),

    HOUR("h"),

    DAYS("d"),

    WEEKS("w"),

    MONTHS("M");

    private static final Map<String, TimeUnits> unitsMap = Arrays.stream(TimeUnits.values())
            .collect(Collectors.toMap(k -> k.unit, Function.identity()));

    private final String unit;

    TimeUnits(String unit) {
        this.unit = unit;
    }

    public static TimeUnits parseTimeUnit(String timeUnit) {
        return Optional.ofNullable(unitsMap.get(timeUnit))
                .orElseThrow(() -> new RuntimeException("no value present for time unit: " + timeUnit));
    }
}
