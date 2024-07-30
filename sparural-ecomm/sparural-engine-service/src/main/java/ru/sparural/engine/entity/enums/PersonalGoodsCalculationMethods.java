package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum PersonalGoodsCalculationMethods {

    Percent("Percent"),
	FixAmount("FixAmount"),
	AmountPerUnit("AmountPerUnit");

    private final String val;

    PersonalGoodsCalculationMethods(String val) {
        this.val = val;
    }

    private final static Map<String, PersonalGoodsCalculationMethods> valMap = Arrays.stream(PersonalGoodsCalculationMethods.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static PersonalGoodsCalculationMethods getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
