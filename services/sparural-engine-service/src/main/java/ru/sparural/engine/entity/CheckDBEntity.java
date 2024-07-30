package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class CheckDBEntity {
    private Long id;
    private Long cardId;
    private Long userId;
    private Long merchantId;
    private Long dateTime;
    private Boolean isRefund;
    private Long checkNumber;
    private Double amount;
    private Long currencyId;
    private String externalPurchaseId;
}
