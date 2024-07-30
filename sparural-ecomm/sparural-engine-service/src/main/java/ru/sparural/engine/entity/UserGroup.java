package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserGroup {
    private Long id;
    private String name;
    private Boolean isSys;
}
