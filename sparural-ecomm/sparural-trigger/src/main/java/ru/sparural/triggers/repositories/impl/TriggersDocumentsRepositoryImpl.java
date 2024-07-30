package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.MessagesTemplates;
import ru.sparural.tables.TriggersDocumentTypes;
import ru.sparural.tables.TriggersDocuments;
import ru.sparural.tables.TriggersTypes;
import ru.sparural.triggerapi.dto.TriggerDto;
import ru.sparural.triggers.dto.TriggerTypes;
import ru.sparural.triggers.entities.TriggerDocument;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class TriggersDocumentsRepositoryImpl implements ru.sparural.triggers.repositories.TriggersDocumentsRepository {

    private final DSLContext dslContext;
    private final TriggersDocuments table = TriggersDocuments.TRIGGERS_DOCUMENTS;

    @Override
    public Optional<TriggerDocument> findByIdAndNameMessageTemplate(String triggerDocumentTypeName, Long triggerDocumentId) {
        return dslContext
                .select()
                .from(table)
                .where(table.TRIGGERS_DOCUMENT_ID.eq(triggerDocumentId))
                .fetchOptionalInto(TriggerDocument.class);
    }

    @Override
    public Optional<TriggerDocument> create(TriggerDocument triggerDocument) {
        return dslContext
                .insertInto(table)
                .set(table.TRIGGERS_TYPE_ID, triggerDocument.getTriggersTypeId())
                .set(table.TRIGGERS_DOCUMENT_TYPE_ID, triggerDocument.getTriggersDocumentTypeId())
                .set(table.TRIGGERS_DOCUMENT_ID, triggerDocument.getTriggersDocumentId())
                .set(table.TIME_UNIT, triggerDocument.getTimeUnit())
                .set(table.DATE_START, triggerDocument.getDateStart())
                .set(table.DATE_END, triggerDocument.getDateEnd())
                .set(table.TIME_START, triggerDocument.getTimeStart())
                .set(table.TIME_END, triggerDocument.getTimeEnd())
                .set(table.FREQUENCY, triggerDocument.getFrequency())
                .set(table.CREATED_AT, new Date().getTime())
                .returning()
                .fetchOptionalInto(TriggerDocument.class);
    }

    @Override
    public Optional<TriggerDocument> update(TriggerDocument triggerDocument, Long triggerDocumentId) {
        return dslContext.update(table)
                .set(table.TRIGGERS_TYPE_ID, triggerDocument.getTriggersTypeId())
                .set(table.TRIGGERS_DOCUMENT_TYPE_ID, triggerDocument.getTriggersDocumentTypeId())
                .set(table.DATE_START, triggerDocument.getDateStart())
                .set(table.DATE_END, triggerDocument.getDateEnd())
                .set(table.TIME_UNIT, triggerDocument.getTimeUnit())
                .set(table.TIME_START, triggerDocument.getTimeStart())
                .set(table.TIME_END, triggerDocument.getTimeEnd())
                .set(table.FREQUENCY, triggerDocument.getFrequency())
                .set(table.CREATED_AT, new Date().getTime())
                .where(table.TRIGGERS_DOCUMENT_ID.eq(triggerDocumentId))
                .returning()
                .fetchOptionalInto(TriggerDocument.class);
    }

    @Override
    public List<TriggerDocument> findByTriggerType(TriggerTypes triggerType) {
        var triggerTypesId = dslContext.select(TriggersTypes.TRIGGERS_TYPES.ID)
                .from(TriggersTypes.TRIGGERS_TYPES)
                .where(TriggersTypes.TRIGGERS_TYPES.CODE.eq(triggerType.getCode()))
                .fetchOptionalInto(Long.class).orElseThrow();
        return dslContext.selectFrom(table).where(table.TRIGGERS_TYPE_ID.eq(triggerTypesId)
                        .and(table.DATE_START.lessOrEqual(new Date().getTime()))
                        .and(table.DATE_END.greaterOrEqual(new Date().getTime())))
                .fetchInto(TriggerDocument.class);
    }

    @Override
    public List<TriggerDto> findAll() {
        return dslContext
                .select()
                .from(table)
                .leftJoin(TriggersDocumentTypes.TRIGGERS_DOCUMENT_TYPES)
                .on(table.TRIGGERS_DOCUMENT_TYPE_ID.eq(TriggersDocumentTypes.TRIGGERS_DOCUMENT_TYPES.ID))
                .fetchInto(TriggerDto.class);
    }

    @Override
    public Optional<TriggerDocument> findByMessageTemplateId(Long messageTemplateId) {
        return dslContext
                .select()
                .from(table)
                .where(table.TRIGGERS_DOCUMENT_ID.eq(messageTemplateId))
                .fetchOptionalInto(TriggerDocument.class);
    }
}
