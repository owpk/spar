package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PersonalGoodsEntity {
    private Long id;
    private Long userId;
    private Long goodsId;
    private Long startDate;
    private Long endDate;
    private String preferenceType;
    private String calculationMethod;
    private Integer preferenceValue;
    private Boolean accepted;
    private String brandId;
    private String priceUp;
    private String priceDown;
}