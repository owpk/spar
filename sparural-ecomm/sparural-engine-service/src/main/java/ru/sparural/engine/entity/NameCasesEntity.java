package ru.sparural.engine.entity;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NameCasesEntity {
    private String nominative;
    private String genitive;
    private String plural;
    private String abbreviation;
}
