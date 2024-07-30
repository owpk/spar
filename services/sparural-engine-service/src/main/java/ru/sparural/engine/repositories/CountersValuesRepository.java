package ru.sparural.engine.repositories;

public interface CountersValuesRepository {
    boolean bindCounterToUser(Long counterId, Long userId);
}
