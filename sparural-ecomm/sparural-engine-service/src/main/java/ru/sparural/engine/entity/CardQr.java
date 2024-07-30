package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CardQr {
    private Long id;
    private Long cardId;
    private Long codeGeneratedDate;
    private String code;
    private Long lifeTime;
}