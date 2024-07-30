package ru.sparural.engine.services;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ContentsService<T> {
    T create(T data);

    T update(String alias, T data);

    boolean delete(String alias);

    T get(String alias);

    List<T> list(int offset, int limit);
}
