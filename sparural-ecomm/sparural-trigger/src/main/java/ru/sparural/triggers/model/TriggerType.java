package ru.sparural.triggers.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum TriggerType {
    MESSAGE_TEMPLATE("messageTemplate");
    private static final Map<EventType, TriggerType> eventTypeTriggerTypeMap;
    static {
        eventTypeTriggerTypeMap = new HashMap<>();
        eventTypeTriggerTypeMap.put(EventType.REG_NOT_COMPLETED, TriggerType.MESSAGE_TEMPLATE);
        eventTypeTriggerTypeMap.put(EventType.MADE_PURCHASE_IN_STORE, TriggerType.MESSAGE_TEMPLATE);
        eventTypeTriggerTypeMap.put(EventType.NO_CONDITION, TriggerType.MESSAGE_TEMPLATE);
    }

    private final String messageTemplate;

    TriggerType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public static TriggerType getByEventType(EventType eventType) {
        return eventTypeTriggerTypeMap.get(eventType);
    }
}