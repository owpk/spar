package ru.sparural.engine.api.enums;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum UserGroups {
    ANON(1);
    private int code;

    UserGroups(int code) {
        this.code = code;
    }
}
