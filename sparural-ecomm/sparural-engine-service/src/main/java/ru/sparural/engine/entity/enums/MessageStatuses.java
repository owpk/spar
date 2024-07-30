package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum MessageStatuses {

    BeingSent("BeingSent"),
	Sent("Sent"),
	NotSent("NotSent");

    private final String val;

    MessageStatuses(String val) {
        this.val = val;
    }

    private final static Map<String, MessageStatuses> valMap = Arrays.stream(MessageStatuses.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static MessageStatuses getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
