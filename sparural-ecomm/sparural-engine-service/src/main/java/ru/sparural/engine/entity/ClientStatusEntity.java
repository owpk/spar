package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ClientStatusEntity {
    private Long id;
    private String name;
    private Integer threshold;
}
