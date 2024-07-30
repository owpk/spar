package ru.sparural.triggers.services;

import ru.sparural.triggerapi.dto.TriggerDto;
import ru.sparural.triggers.dto.TriggerTypes;
import ru.sparural.triggers.entities.TriggerDocument;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
public interface TriggersDocumentsService {
    TriggerDto findByIdAndNameMessageTemplate(String messageTemplateName, Long messageTemplateId);

    TriggerDocument findByMessageTemplateId(Long messageTemplateId);

    List<TriggerDto> findAll();

    TriggerDto create(TriggerDocument triggerDocument);

    TriggerDto update(TriggerDocument triggerDocument, Long triggerDocumentId);

    List<TriggerDocument> findByTriggerType(TriggerTypes triggerType);
}
