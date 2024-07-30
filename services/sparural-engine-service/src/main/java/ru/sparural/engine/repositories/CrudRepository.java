package ru.sparural.engine.repositories;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface CrudRepository<L, T> {

    L insert(T t);

    Integer update(T t);

    Optional<T> get(Long id);

    Integer delete(Long id);

}