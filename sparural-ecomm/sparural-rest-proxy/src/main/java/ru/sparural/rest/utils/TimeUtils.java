package ru.sparural.rest.utils;

import java.time.Instant;

/**
 * @author Vorobyev Vyacheslav
 */
public class TimeUtils {

    public static Long generateCurrentTime() {
        return Instant.now().getEpochSecond();
    }
}
