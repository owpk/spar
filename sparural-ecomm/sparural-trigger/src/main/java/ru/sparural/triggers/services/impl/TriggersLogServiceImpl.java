package ru.sparural.triggers.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sparural.triggerapi.dto.TriggerLogDto;
import ru.sparural.triggers.entities.TriggerLog;
import ru.sparural.triggers.exceptions.ResourceNotFoundException;
import ru.sparural.triggers.repositories.TriggersLogRepository;
import ru.sparural.triggers.utils.DtoMapperUtils;

@Service
@RequiredArgsConstructor
public class TriggersLogServiceImpl implements ru.sparural.triggers.services.TriggersLogService {
    private final TriggersLogRepository triggersLogRepository;
    private final DtoMapperUtils dtoMapperUtils;

    @Override
    @Transactional
    public TriggerLogDto save(TriggerLogDto triggerLogDto, Long userId) {
        return dtoMapperUtils.convert(triggersLogRepository
                .save(dtoMapperUtils.convert(triggerLogDto, TriggerLog.class), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Запись не была создана")), TriggerLogDto.class);
    }

    @Override
    public TriggerLogDto findByDocumentId(Long documentId, Long userId) {
        var triggerLog = new TriggerLog();
        triggerLog.setDatetime(0L);
        return dtoMapperUtils.convert(triggersLogRepository.findByDocumentId(documentId, userId)
                .orElse(triggerLog), TriggerLogDto.class);
    }
}
