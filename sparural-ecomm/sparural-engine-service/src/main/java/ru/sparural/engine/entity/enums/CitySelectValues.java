package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CitySelectValues {

    All("All"),
	Nowhere("Nowhere"),
	Selection("Selection");

    private final String val;

    CitySelectValues(String val) {
        this.val = val;
    }

    private final static Map<String, CitySelectValues> valMap = Arrays.stream(CitySelectValues.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static CitySelectValues getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
