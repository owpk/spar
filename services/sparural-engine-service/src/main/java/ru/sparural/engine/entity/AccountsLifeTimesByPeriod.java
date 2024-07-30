package ru.sparural.engine.entity;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountsLifeTimesByPeriod {
    private Long id;
    private Long accountId;
    private Integer activationAmount;
    private Integer expirationAmount;
    private String period;
}
