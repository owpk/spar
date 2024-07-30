package ru.sparural.engine.api.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponDto {
    Long id;
    @JsonIgnore
    Long couponEmmissionsId;
    String code;
    String qrContent;
    String couponState;
    CouponEmissionsDto emission;
}
