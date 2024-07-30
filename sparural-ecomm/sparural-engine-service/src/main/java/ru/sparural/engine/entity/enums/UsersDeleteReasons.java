package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum UsersDeleteReasons {

    Reject("Reject"),
	Ban("Ban");

    private final String val;

    UsersDeleteReasons(String val) {
        this.val = val;
    }

    private final static Map<String, UsersDeleteReasons> valMap = Arrays.stream(UsersDeleteReasons.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static UsersDeleteReasons getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
