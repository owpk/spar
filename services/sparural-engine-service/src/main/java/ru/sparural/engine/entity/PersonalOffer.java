package ru.sparural.engine.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalOffer {
    private Long id;
    private String attribute;
    private String title;
    private String description;
    private Long begin;
    private Long end;
    private Boolean isPublic;
    private Boolean draft;
}
