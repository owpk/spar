package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ConfirmCodeStatuses {

    Request("Request"),
	Success("Success"),
	Rejected("Rejected");

    private final String val;

    ConfirmCodeStatuses(String val) {
        this.val = val;
    }

    private final static Map<String, ConfirmCodeStatuses> valMap = Arrays.stream(ConfirmCodeStatuses.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static ConfirmCodeStatuses getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
