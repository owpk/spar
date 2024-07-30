package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CardStatuses {

    ForIssue("ForIssue"),
	Emitted("Emitted"),
	Activated("Activated"),
	Replaced("Replaced"),
	Expired("Expired"),
	Prepared("Prepared");

    private final String val;

    CardStatuses(String val) {
        this.val = val;
    }

    private final static Map<String, CardStatuses> valMap = Arrays.stream(CardStatuses.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static CardStatuses getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
