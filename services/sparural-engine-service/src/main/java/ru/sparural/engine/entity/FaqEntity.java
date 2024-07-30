package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FaqEntity {
    private Long id;
    private String question;
    private String answer;
    private Integer order;
}