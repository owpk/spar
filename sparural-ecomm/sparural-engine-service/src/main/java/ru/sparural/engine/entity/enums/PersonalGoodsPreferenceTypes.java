package ru.sparural.engine.entity.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum PersonalGoodsPreferenceTypes {

    Cashback("Cashback"),
	Discount("Discount");

    private final String val;

    PersonalGoodsPreferenceTypes(String val) {
        this.val = val;
    }

    private final static Map<String, PersonalGoodsPreferenceTypes> valMap = Arrays.stream(PersonalGoodsPreferenceTypes.values())
            .collect(Collectors.toMap(x -> x.val, Function.identity()));

    public static PersonalGoodsPreferenceTypes getByVal(String val) {
        return Optional.ofNullable(valMap.get(val))
                .orElseThrow(() -> new RuntimeException("No value found by key: " + val));
    }
}
