package ru.sparural.triggers.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ru.sparural.tables.JobDetailsTriggers;
import ru.sparural.triggers.entities.JobDetailsTriggerEntity;
import ru.sparural.triggers.repositories.JobDetailsTriggerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobDetailsTriggerRepositoryImpl implements JobDetailsTriggerRepository {
    private final DSLContext dslContext;
    private final JobDetailsTriggers jobDetailsTriggersTable = JobDetailsTriggers.JOB_DETAILS_TRIGGERS;

    @Override
    public List<JobDetailsTriggerEntity> getByTriggerDocumentId(Long documentId) {
        return dslContext.selectFrom(jobDetailsTriggersTable)
                .where(jobDetailsTriggersTable.TRIGGER_DOCUMENT_ID.eq(documentId))
                .fetchInto(JobDetailsTriggerEntity.class);
    }

    @Override
    public long deleteByTriggerDocumentId(long documentId) {
        try(var delete = dslContext.delete(jobDetailsTriggersTable)) {
            return delete.where(jobDetailsTriggersTable.TRIGGER_DOCUMENT_ID.eq(documentId)).execute();
        }
    }

    @Override
    public JobDetailsTriggerEntity create(JobDetailsTriggerEntity entity) {
        return dslContext.insertInto(jobDetailsTriggersTable)
                .set(jobDetailsTriggersTable.JOB_GROUP, entity.getJobGroup())
                .set(jobDetailsTriggersTable.JOB_NAME, entity.getJobName())
                .set(jobDetailsTriggersTable.TRIGGER_DOCUMENT_ID, entity.getTriggerDocumentId())
                .returning().fetchOneInto(JobDetailsTriggerEntity.class);
    }

}
