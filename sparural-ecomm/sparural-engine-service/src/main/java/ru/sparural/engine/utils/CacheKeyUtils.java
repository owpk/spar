package ru.sparural.engine.utils;

import java.text.DecimalFormat;

/**
 * @author Vorobyev Vyacheslav
 */
public final class CacheKeyUtils {
    private static final DecimalFormat df = new DecimalFormat("0.0000");

    public static String computeDoubleKey(Double key) {
        return key == null ? "" : df.format(key);
    }
}
