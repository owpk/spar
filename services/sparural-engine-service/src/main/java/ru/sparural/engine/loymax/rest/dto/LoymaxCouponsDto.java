package ru.sparural.engine.loymax.rest.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxCouponsDto {
    Long id;
    String code;
    String qrContent;
    String createDate;
    String updateDate;
    String endDate;
    String couponState;
    String emissionTitle;
}
