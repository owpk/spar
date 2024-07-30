package ru.sparural.rest.api.validators.utils;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Vorobyev Vyacheslav
 */
public class ReflectUtils {

    public static boolean checkIfSomeFieldsIsPresent(Object o) {
        if (o == null) return false;
        var fields = o.getClass().getDeclaredFields();
        return Stream.of(fields).map(x -> {
            try {
                x.setAccessible(true);
                return x.get(o);
            } catch (IllegalAccessException e) {
                return null;
            }
        }).anyMatch(Objects::nonNull);
    }
}
