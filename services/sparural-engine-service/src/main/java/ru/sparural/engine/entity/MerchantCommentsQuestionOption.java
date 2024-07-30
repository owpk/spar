package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MerchantCommentsQuestionOption {
    private Long id;
    private String questionId;
    private String answer;
}
