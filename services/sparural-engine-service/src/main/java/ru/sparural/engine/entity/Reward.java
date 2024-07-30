package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Reward {
    private Long id;
    private String rewardType;
    private String description;
    private Double amount;
    private Long currenciesId;
    private Long checkId;
    private Long currencyId;
}
