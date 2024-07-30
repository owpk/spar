package ru.sparural.engine.api.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PersonalGoodsDto {
    Long id;
    GoodsDto goods;
    Long startDate;
    Long endDate;
    String preferenceType;
    String calculationMethod;
    Integer preferenceValue;
    Boolean accepted;
    String priceUp;
    String priceDown;
}
