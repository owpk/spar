package ru.sparural.engine.entity;


import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MerchantCommentsQuestion {
    private String code;
    private String question;
    private String grade;
    private String type;
    private List<Answer> options;
}
