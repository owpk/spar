package ru.sparural.engine.repositories;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserStatusRepository {

    void bind(Long id, Long userId, Integer currentValue, Integer leftUntilNextStatus);
}
