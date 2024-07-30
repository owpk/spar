package ru.sparural.engine.utils;

import java.util.concurrent.TimeUnit;

public class TimeHelper {

    public static Long currentTime() {
        return TimeUnit.SECONDS.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}