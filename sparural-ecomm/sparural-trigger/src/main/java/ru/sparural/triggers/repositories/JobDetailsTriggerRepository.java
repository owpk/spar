package ru.sparural.triggers.repositories;

import org.jooq.Field;
import ru.sparural.triggers.entities.JobDetailsTriggerEntity;

import java.util.List;

public interface JobDetailsTriggerRepository {

    List<JobDetailsTriggerEntity> getByTriggerDocumentId(Long documentId);

    long deleteByTriggerDocumentId(long documentId);

    JobDetailsTriggerEntity create(JobDetailsTriggerEntity entity);
}
