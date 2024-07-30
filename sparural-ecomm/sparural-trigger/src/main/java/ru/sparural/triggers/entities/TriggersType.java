package ru.sparural.triggers.entities;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TriggersType {
    private Long id;
    private String code;
    private String name;
}
