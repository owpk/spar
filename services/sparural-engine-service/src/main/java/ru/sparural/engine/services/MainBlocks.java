package ru.sparural.engine.services;

import java.util.List;

public interface MainBlocks<T> {
    List<T> list(int offset, int limit);

    T update(T body, String code);
}
