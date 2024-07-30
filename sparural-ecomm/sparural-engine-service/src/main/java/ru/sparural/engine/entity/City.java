package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class City {
    private Long id;
    private String name;
    private String timezone;
//    private Long createdAt;
//    private Long updatedAt;
}
