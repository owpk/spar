package ru.sparural.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CouponEmission {
    private Long id;
    private String title;
    private Long start;
    private Long end;
    private Boolean isPublic;
}
