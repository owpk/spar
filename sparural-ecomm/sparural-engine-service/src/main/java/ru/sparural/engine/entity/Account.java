package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Account {
    private Long id;
    private Long userId;
    private Long currencyId;
    private Double amount;
    private Double notActivatedAmount;
}
