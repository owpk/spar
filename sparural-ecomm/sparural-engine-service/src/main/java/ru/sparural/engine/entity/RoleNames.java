package ru.sparural.engine.entity;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum RoleNames {
    ANONYMOUS("anonymous"),
    CLIENT("client");

    private final String name;

    RoleNames(String name) {
        this.name = name;
    }
}
