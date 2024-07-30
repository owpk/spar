package ru.sparural.engine.services;

/**
 * @author Vorobyev Vyacheslav
 */
public interface AuthSettingsService<T> {
    T update(T entity);

    T get();
}
