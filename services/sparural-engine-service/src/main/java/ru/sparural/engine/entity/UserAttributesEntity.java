package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserAttributesEntity {
    private Long id;
    private String attributeName;
    private String name;
}
