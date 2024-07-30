package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Offer {
    private Long id;
    private String title;
    private String description;
    private String shortDescription;
    private Long begin;
    private Long end;
}
