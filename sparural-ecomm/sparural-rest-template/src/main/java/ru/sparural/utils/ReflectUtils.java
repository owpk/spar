package ru.sparural.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vorobyev Vyacheslav
 */
// TODO заменить на что нибудь от google или apache
//  или добавить рекурсию для апдейта вложенных объектов
public class ReflectUtils {

    public static void updateAllFields(Object source, Object target) {
        objectFieldsForEach(x -> true, source, target);
    }

    public static void updateNotNullFields(Object source, Object target) {
        objectFieldsForEach(Objects::nonNull, source, target);
    }

    private static void objectFieldsForEach(Predicate<Object> predicate, Object sourceObject, Object targetObject) {
        var sourceFields = getFieldsToLowerCase(sourceObject);
        var targetFields = getFieldsToLowerCase(targetObject);
        sourceFields.forEach((k, v) -> {
            try {
                var sourceFieldValue = v.get(sourceObject);
                var targetField = targetFields.get(k);
                if (predicate.test(sourceFieldValue) && targetField != null) {
                    if (!targetField.getType().equals(v.getType())) {
                        sourceFieldValue = tryToHandleEnum(targetField, v, sourceFieldValue);
                    }
                    targetField.set(targetObject, sourceFieldValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    private static Object tryToHandleEnum(Field targetField, Field sourceField, Object sourceFieldValue) throws IllegalAccessException {
        Predicate<Field> isEnum = x -> x.getType().isEnum();
        if (isEnum.test(targetField)) {
            if (sourceFieldValue instanceof String) {
                String actual = (String) sourceFieldValue;
                return getEnumFromField(targetField, actual);
            }
        } else if (isEnum.test(sourceField)) {
            if (targetField.getType().equals(String.class)) {
                var en = (Enum) sourceFieldValue;
                var meths = en.getClass().getDeclaredMethods();
                var getter = Stream.of(meths)
                        .filter(x -> x.getName().startsWith("get"))
                        .findAny();
                if (getter.isPresent()) {
                    var method = getter.get();
                    try {
                        return method.invoke(en);
                    } catch (InvocationTargetException e) {
                        return null;
                    }
                }
                return en.name();
            }
        }
        return null;
    }

    private static Enum getEnumFromField(Field f, String val) {
        return Enum.valueOf((Class<Enum>) f.getType(), val);
    }

    private static Map<String, Field> getFieldsToLowerCase(Object o) {
        var fields = o.getClass().getDeclaredFields();
        return Stream.of(fields).peek(x -> x.setAccessible(true))
                .collect(Collectors.toMap(
                        x -> x.getName().toLowerCase(Locale.ROOT), x -> x));
    }

}
