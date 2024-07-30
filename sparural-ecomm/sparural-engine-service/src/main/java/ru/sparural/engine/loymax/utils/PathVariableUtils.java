package ru.sparural.engine.loymax.utils;

import ru.sparural.engine.loymax.LoymaxConstants;

/**
 * @author Vorobyev Vyacheslav
 */
public class PathVariableUtils {

    public static String replacePathVariable(String url, String variable) {
        return url.replaceAll(LoymaxConstants.PATH_VARIABLE_PATTERN, variable);
    }
}
