package ru.sparural.engine.loymax.rest.dto.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Attribute {
    Long id;
    String startFillDate;
    String endFillDate;
    Long maxGoodsCount;
    GoodsGroup goodsGroup;
}
