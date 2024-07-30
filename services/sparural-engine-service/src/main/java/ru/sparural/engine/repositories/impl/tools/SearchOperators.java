package ru.sparural.engine.repositories.impl.tools;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum SearchOperators {
    EQUAL("="),
    NOT_EQUAL("<>"),
    MIN("min"),
    MAX("max"),
    LIKE("LIKE");

    private final String value;

    SearchOperators(String value) {
        this.value = value;
    }
}
