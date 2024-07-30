package ru.sparural.engine.services;

import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ExternalDocumentsService<T> {
    List<T> list(int offset, int limit);

    T get(String alias) throws ResourceNotFoundException;

    T create(T data);

    T update(String alias, T data);

    boolean delete(String alias);
}
