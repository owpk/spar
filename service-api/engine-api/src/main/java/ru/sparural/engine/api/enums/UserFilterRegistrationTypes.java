package ru.sparural.engine.api.enums;

public enum UserFilterRegistrationTypes {
    ALL,
    REGISTRED,
    NO_REGISTRED;

    public static boolean isNeedFilter(UserFilterRegistrationTypes type) {
        return type==null || !UserFilterRegistrationTypes.ALL.equals(type);
    }
}
