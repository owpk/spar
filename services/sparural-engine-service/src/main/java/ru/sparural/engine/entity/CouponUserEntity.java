package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponUserEntity {
    private Long id;
    private Long couponId;
    private Long userId;
}
