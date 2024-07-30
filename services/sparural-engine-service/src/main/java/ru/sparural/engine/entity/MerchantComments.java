package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MerchantComments {
    private Long id;
    private Long merchantId;
    private Integer grade;
    private String comment;
    private List<Question> questions;
}
