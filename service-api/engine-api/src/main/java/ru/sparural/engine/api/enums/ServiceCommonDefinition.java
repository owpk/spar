package ru.sparural.engine.api.enums;

import lombok.Getter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum ServiceCommonDefinition {
    SERVICE_NAME("lp.dp"),
    REQUEST_TOPIC("lp.db.request"),
    RESPONSE_TOPIC("lp.db.response");

    String value;

    ServiceCommonDefinition(String value) {
        this.value = value;
    }
}
