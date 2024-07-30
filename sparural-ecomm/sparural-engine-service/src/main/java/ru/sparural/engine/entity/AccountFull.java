package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@ToString
public class AccountFull {
    private Long id;
    private Long userId;
    private CurrencyEntity currency;
    private Double amount;
    private Double notActivatedAmount;
    private Map<Long, AccountsLifeTimesByTime> accountLifeTimeByTime = new HashMap<>();
    private Map<Long, AccountsLifeTimesByPeriod> accountLifeTimeByPeriod = new HashMap<>();
}
