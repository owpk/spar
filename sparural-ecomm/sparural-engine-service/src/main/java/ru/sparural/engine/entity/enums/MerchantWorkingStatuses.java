package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum MerchantWorkingStatuses {

    Open("Open"),
	OnRepair("OnRepair"),
	Closed("Closed");

    private final String val;

    MerchantWorkingStatuses(String val) {
        this.val = val;
    }

    private final static Map<String, MerchantWorkingStatuses> valMap = Arrays.stream(MerchantWorkingStatuses.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static MerchantWorkingStatuses getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
