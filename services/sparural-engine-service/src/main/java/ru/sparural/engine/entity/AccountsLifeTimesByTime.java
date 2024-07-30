package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccountsLifeTimesByTime {
    private Long id;
    private Long accountId;
    private Integer amount;
    private Long date;
}
