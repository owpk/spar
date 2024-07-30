package ru.sparural.gobals;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum RoleNames {
    ANONYMOUS("anonimous"),
    CLIENT("client"),
    ADMIN("admin"),
    MANAGER("manager");
    String name;

    RoleNames(String name) {
        this.name = name;
    }
}
