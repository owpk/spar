package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Genders {

    male("male"),
	female("female"),
	other("other");

    private final String val;

    Genders(String val) {
        this.val = val;
    }

    private final static Map<String, Genders> valMap = Arrays.stream(Genders.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static Genders getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
