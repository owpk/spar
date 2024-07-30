package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AccountType {
    private Long id;
    private String name;
    private String currency;
    private Long currenciesId;
    private Integer order;
}
