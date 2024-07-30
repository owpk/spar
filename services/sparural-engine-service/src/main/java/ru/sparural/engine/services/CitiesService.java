package ru.sparural.engine.services;

import java.util.List;

public interface CitiesService<T> {
    List<T> list(int offset, int limit);

    T getByName(String name);

    String getTimezoneById(Long id);
}
