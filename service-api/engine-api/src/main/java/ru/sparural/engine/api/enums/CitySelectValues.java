package ru.sparural.engine.api.enums;

import lombok.Getter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum CitySelectValues {
    ALL("All"),
    NOWHERE("Nowhere"),
    SELECTION("Selection");

    private static final Map<String, CitySelectValues> valuesMap =
            Stream.of(CitySelectValues.values())
                    .collect(Collectors.toMap(x -> x.val, x -> x));
    private String val;

    CitySelectValues(String value) {
        this.val = value;
    }

    public static CitySelectValues of(String string) {
        if (!valuesMap.containsKey(string))
            return null;
        return valuesMap.get(string);
    }
}
