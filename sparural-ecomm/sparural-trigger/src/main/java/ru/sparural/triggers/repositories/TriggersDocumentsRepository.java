package ru.sparural.triggers.repositories;

import ru.sparural.triggerapi.dto.TriggerDto;
import ru.sparural.triggers.dto.TriggerTypes;
import ru.sparural.triggers.entities.TriggerDocument;

import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersDocumentsRepository {
    Optional<TriggerDocument> findByIdAndNameMessageTemplate(String triggerDocumentTypeName, Long triggerDocumentId);

    Optional<TriggerDocument> create(TriggerDocument triggerDocument);

    Optional<TriggerDocument> update(TriggerDocument triggerDocument, Long triggerDocumentId);

    List<TriggerDocument> findByTriggerType(TriggerTypes triggerType);

    List<TriggerDto> findAll();

    Optional<TriggerDocument> findByMessageTemplateId(Long messageTemplateId);
}
