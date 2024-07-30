package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum CouponStates {

    Created("Created"),
	Issued("Issued"),
	Used("Used"),
	Rejected("Rejected"),
	QueuedToUse("QueuedToUse"),
	QueuedToIssue("QueuedToIssue");

    private final String val;

    CouponStates(String val) {
        this.val = val;
    }

    private final static Map<String, CouponStates> valMap = Arrays.stream(CouponStates.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static CouponStates getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
