package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum LifeTimesPeriods {

    FromWeekToMonth("FromWeekToMonth"),
	FromMonthToYear("FromMonthToYear"),
	FromYear("FromYear");

    private final String val;

    LifeTimesPeriods(String val) {
        this.val = val;
    }

    private final static Map<String, LifeTimesPeriods> valMap = Arrays.stream(LifeTimesPeriods.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static LifeTimesPeriods getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
