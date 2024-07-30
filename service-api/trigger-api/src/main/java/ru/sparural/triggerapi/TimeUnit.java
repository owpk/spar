package ru.sparural.triggerapi;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
public enum TimeUnit {
    SECONDS("s"),
    MINUTES("m"),
    HOURS("h"),
    MONTH("M");

    private final String timeUnit;

    TimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
