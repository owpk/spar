package ru.sparural.triggers.entities;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TriggerLog {
    private Long id;
    private Long triggersDocumentId;
    private Long triggersDocumentTypeId;
    private Long triggersTypeId;
    private Long datetime;
    private String data;
}
