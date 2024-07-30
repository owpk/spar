package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.TriggersType;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersTypesRepository {
    List<TriggersType> fetch(int offset, int limit);

    List<TriggersType> fetchAll();

    Optional<TriggersType> get(Long id);
}
