package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CheckEntity {
    private Long id;
    private Long cardId;
    private Long dateTime;
    private Long userId;
    private String externalPurchaseId;
    private Boolean isRefund;
    private Long checkNumber;
    private Double amount;
    private Long currenciesId;
    private Long merchantsId;
    private List<Item> items;
    private List<Withdraw> withdraws;
    private List<Reward> rewards;
}
