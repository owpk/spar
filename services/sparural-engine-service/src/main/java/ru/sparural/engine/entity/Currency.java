package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Currency {
    private Long id;
    private String name;
    private String description;
    private Boolean isDeleted;
    private NameCasesEntity nameCases;
}
