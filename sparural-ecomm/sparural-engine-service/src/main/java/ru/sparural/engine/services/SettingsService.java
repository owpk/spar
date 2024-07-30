package ru.sparural.engine.services;

/**
 * @author Vorobyev Vyacheslav
 */
public interface SettingsService<T> {
    T update(T data);

    T get();
}
