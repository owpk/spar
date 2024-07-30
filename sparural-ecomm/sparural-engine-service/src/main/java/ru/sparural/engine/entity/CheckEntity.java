package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Long currencyId;
    private CurrencyEntity currency;
    private Long merchantId;
    private Merchant merchant;
    private Map<Long, Item> items = new HashMap<>();
    private Map<Long, Withdraw> withdraws = new HashMap<>();
    private Map<Long, Reward> rewards = new HashMap<>();
}
