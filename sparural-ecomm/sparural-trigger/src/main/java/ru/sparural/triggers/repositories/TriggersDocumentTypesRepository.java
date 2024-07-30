package ru.sparural.triggers.repositories;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersDocumentTypesRepository {
    Optional<Long> findIdByName(String name);
}
