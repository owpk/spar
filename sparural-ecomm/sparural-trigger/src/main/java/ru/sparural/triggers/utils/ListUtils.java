package ru.sparural.triggers.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Vorobyev Vyacheslav
 */
public class ListUtils {
    public static <E> Map<Integer, List<E>> splitList(List<E> elements, Integer partSize) {
        return IntStream.range(0, elements.size()).boxed()
                .collect(Collectors.groupingBy(part -> (part / partSize),
                        Collectors.mapping(elements::get, Collectors.toList())));
    }
}
