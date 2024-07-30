package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Account {
    private Long id;
    private Long userId;
    private Long accountTypeIdField;
    private Double amount;
    private Double notActivatedAmount;
}
