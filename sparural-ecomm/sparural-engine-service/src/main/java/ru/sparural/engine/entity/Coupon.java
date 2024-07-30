package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;


@Data
@ToString
public class Coupon {
    private Long id;
    private Long couponEmmissionsId;
    private String code;
    private String qrContent;
    private String couponState;
    private CouponEmission emission;

}
