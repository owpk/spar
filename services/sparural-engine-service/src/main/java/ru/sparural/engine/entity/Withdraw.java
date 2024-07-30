package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Withdraw {
    private Long id;
    private String withdrawType;
    private String description;
    private Double amount;
    private Long currenciesId;
    private Long currencyId;
    private CurrencyEntity currencyEntity;
    private Long checkId;
}
