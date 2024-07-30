package ru.sparural.engine.loymax.rest.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@ToString

@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxPersonalGoodsResponseDto {
    @JsonProperty(value = "StartDate")
    String startDate;
    @JsonProperty(value = "EndDate")
    String endDate;
    @JsonProperty(value = "Preferences")
    List<LoymaxPreferencesDto> preferences;
}
