package ru.sparural.engine.loymax.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@RequiredArgsConstructor
public enum PersonalGoodsName {
    PERSONAL_OFFERS_GOODS_PRICE("PersonalOffersGoodsPrice"),
    PERSONAL_OFFERS_GOODS("PersonalOffersGoods");

    private final String name;

    private static final Map<String, PersonalGoodsName> BY_VALUES_MAP = Stream.of(PersonalGoodsName.values())
            .collect(Collectors.toMap(PersonalGoodsName::getName, v -> v));

    public static PersonalGoodsName fromString(String value) {
        return Optional
                .ofNullable(BY_VALUES_MAP.get(value))
                .orElseThrow(() -> new IllegalArgumentException("Invalid PersonalGoodsName value: " + value));
    }
}
