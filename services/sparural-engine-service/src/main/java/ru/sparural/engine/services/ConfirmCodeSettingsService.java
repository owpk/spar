package ru.sparural.engine.services;

/**
 * @author Vorobyev Vyacheslav
 */
public interface ConfirmCodeSettingsService<T> {
    T update(T data);

    T get();
}
