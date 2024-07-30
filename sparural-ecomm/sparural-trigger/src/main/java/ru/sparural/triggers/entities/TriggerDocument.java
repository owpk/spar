package ru.sparural.triggers.entities;

import lombok.Data;
import lombok.ToString;

/**
 * @author Vorobyev Vyacheslav
 */
@Data
@ToString
public class TriggerDocument {
    private Long id;
    private Long triggersTypeId;
    private Long triggersDocumentTypeId;
    private Long triggersDocumentId;
    private Long dateStart;
    private Long dateEnd;
    private Integer frequency;
    private String timeStart;
    private String timeEnd;
    private String timeUnit;
}
