package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CurrencyEntity {
    private Long id;
    private String name;
    private String description;
    private Boolean isDeleted;
    private String externalId;
    private Object nameCases;
}
