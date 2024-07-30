package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class NameCasesEntity {
    private String nominative;
    private String genitive;
    private String plural;
    private String abbreviation;
}
