package ru.sparural.triggers.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum EventType implements Serializable {
    REG_NOT_COMPLETED("in-progress"),
    NO_CONDITION("without-conditions"),
    MADE_PURCHASE_IN_STORE("the-purchase-is-completed"),
    LIFESPAN_OF_CURRENCY("lifespan-of-currency"),
    NO_PURCHASE_FOR_N_DAYS("no-purchase-for-n-days");

    private static final Map<String, EventType> eventTypeMap = Stream.of(EventType.values())
            .collect(Collectors.toMap(x -> x.eventTypeName, x -> x));

    private final String eventTypeName;

    EventType(String eventType) {
        this.eventTypeName = eventType;
    }

    public static EventType of(String typeName) {
        return eventTypeMap.get(typeName);
    }
}
