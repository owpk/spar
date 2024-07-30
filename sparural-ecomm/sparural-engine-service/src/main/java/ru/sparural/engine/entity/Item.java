package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Item {
    private Long id;
    private Integer positionId;
    private String description;
    private Double count;
    private String unit;
    private Double amount;
    private Long checkId;
    private String externalId;
}
