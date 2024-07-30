package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.TriggerLogDto;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersLogService {
    TriggerLogDto save(TriggerLogDto triggerLogDto, Long userId);

    TriggerLogDto findByDocumentId(Long documentId, Long userId);
}
