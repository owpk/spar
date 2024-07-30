package ru.sparural.triggers.entities;

import lombok.Data;

@Data
public class JobDetailsTriggerEntity {

    private Long id;
    private Long triggerDocumentId;
    private String jobName;
    private String jobGroup;
}
