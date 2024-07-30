package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.triggerapi.dto.TriggerDto;
import ru.sparural.triggerapi.dto.TriggersTypeDTO;
import ru.sparural.triggers.dto.TriggerTypes;
import ru.sparural.triggers.entities.TriggerDocument;
import ru.sparural.triggers.exceptions.ResourceNotFoundException;
import ru.sparural.triggers.repositories.TriggersDocumentsRepository;
import ru.sparural.triggers.services.TriggersTypeService;
import ru.sparural.triggers.utils.DtoMapperUtils;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class TriggersDocumentsServiceImpl implements ru.sparural.triggers.services.TriggersDocumentsService {

    private final TriggersDocumentsRepository triggersDocumentsRepository;
    private final TriggersTypeService triggersTypeService;
    private final DtoMapperUtils mapperUtils;

    @Override
    public TriggerDto findByIdAndNameMessageTemplate(String messageTemplateName, Long messageTemplateId) {
        TriggerDocument triggerDocument = triggersDocumentsRepository
                .findByIdAndNameMessageTemplate(messageTemplateName, messageTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException("Trigger with this template of message not exist"));
        TriggersTypeDTO triggersTypeDTO = triggersTypeService.get(triggerDocument.getTriggersTypeId());
        return TriggerDto.builder()
                .id(triggerDocument.getId())
                .triggerType(triggersTypeDTO)
                .dateStart(triggerDocument.getDateStart())
                .dateEnd(triggerDocument.getDateEnd())
                .frequency(triggerDocument.getFrequency())
                .timeStart(triggerDocument.getTimeStart())
                .timeEnd(triggerDocument.getTimeEnd())
                .timeUnit(triggerDocument.getTimeUnit())
                .build();
    }

    @Override
    public TriggerDocument findByMessageTemplateId(Long messageTemplateId) {
        return triggersDocumentsRepository
                .findByMessageTemplateId(messageTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException("Trigger with this template of message not exist"));
    }

    @Override
    public List<TriggerDto> findAll() {
        return null;
    }

    @Override
    @Transactional
    public TriggerDto create(TriggerDocument triggerDocument) {
        var dto = mapperUtils.convert(triggersDocumentsRepository
                .create(triggerDocument)
                .orElseThrow(() -> new ResourceNotFoundException("Trigger with this template of message not exist")), TriggerDto.class);
        var type = triggersTypeService.get(triggerDocument.getTriggersTypeId());
        dto.setTriggerType(type);
        return dto;
    }

    @Override
    @Transactional
    public TriggerDto update(TriggerDocument triggerDocument, Long triggerDocumentId) {
        var dto = mapperUtils.convert(triggersDocumentsRepository
                .update(triggerDocument, triggerDocumentId)
                .orElseThrow(() -> new ResourceNotFoundException("Trigger with this template of message not exist")), TriggerDto.class);
        var type = triggersTypeService.get(triggerDocument.getTriggersTypeId());
        dto.setTriggerType(type);
        return dto;
    }

    @Override
    public List<TriggerDocument> findByTriggerType(TriggerTypes triggerType) {
        return triggersDocumentsRepository.findByTriggerType(triggerType);
    }

}