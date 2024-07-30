package ru.sparural.triggers.repositories;

import ru.sparural.triggers.entities.TriggerLog;

import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersLogRepository {
    Optional<TriggerLog> findByDocumentId(Long documentId, Long userId);

    Optional<TriggerLog> save(TriggerLog triggerLog, Long userId);
}
