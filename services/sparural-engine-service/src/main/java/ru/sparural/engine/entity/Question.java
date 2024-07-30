package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Question {
    private String questionsId;
    private Long optionId;
}
