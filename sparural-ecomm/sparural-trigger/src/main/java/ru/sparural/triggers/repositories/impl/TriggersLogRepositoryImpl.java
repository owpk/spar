package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.TriggersLogs;
import ru.sparural.triggers.entities.TriggerLog;
import ru.sparural.triggers.utils.TimeHelper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TriggersLogRepositoryImpl implements ru.sparural.triggers.repositories.TriggersLogRepository {
    private final DSLContext dslContext;
    private final TriggersLogs table = TriggersLogs.TRIGGERS_LOGS;

    @Override
    public Optional<TriggerLog> findByDocumentId(Long documentId, Long userId) {
        return dslContext.selectFrom(table)
                .where(table.TRIGGERS_DOCUMENT_ID.eq(documentId)
                        .and(table.USERID.eq(userId)))
                .orderBy(table.ID.desc())
                .limit(1)
                .fetchOptionalInto(TriggerLog.class);
    }

    @Override
    public Optional<TriggerLog> save(TriggerLog triggerLog, Long userId) {
        return dslContext.insertInto(table)
                .set(table.TRIGGERS_DOCUMENT_ID, triggerLog.getTriggersDocumentId())
                .set(table.TRIGGERS_TYPE_ID, triggerLog.getTriggersTypeId())
                .set(table.TRIGGERS_DOCUMENT_TYPE_ID, triggerLog.getTriggersDocumentTypeId())
                .set(table.DATETIME, triggerLog.getDatetime())
                .set(table.USERID, userId)
                .set(table.CREATED_AT, TimeHelper.currentTime())
                .returning()
                .fetchOptionalInto(TriggerLog.class);
    }
}
