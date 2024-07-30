package ru.sparural.triggers.dto;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum TriggerTypes {

    NO_CONDITIONS("no-conditions"),
    MADE_PURCHASE_IN_STORE("made-purchase-in-store"),
    REGISTRATION_NOT_COMPLETE("registration-not-completed");

    private final String code;

    TriggerTypes(String code) {
        this.code = code;
    }
}
