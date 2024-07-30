package ru.sparural.engine.loymax.rest.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxItemsDto {
    @JsonProperty(value = "GoodsId")
    String goodsId;
    @JsonProperty(value = "PreferenceType")
    String preferenceType;
    @JsonProperty(value = "CalculationMethod")
    String calculationMethod;
    @JsonProperty(value = "PriceUp")
    String priceUp;
    @JsonProperty(value = "PriceDown")
    String priceDown;
    @JsonProperty(value = "PreferenceValue")
    String preferenceValue;
    @JsonProperty(value = "Accepted")
    Boolean accepted;
}
