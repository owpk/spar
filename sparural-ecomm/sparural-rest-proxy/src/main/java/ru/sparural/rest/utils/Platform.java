package ru.sparural.rest.utils;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum Platform {
    WEB("web"),
    MOBILE("mobile");

    private final String name;

    Platform(String name) {
        this.name = name;
    }
}