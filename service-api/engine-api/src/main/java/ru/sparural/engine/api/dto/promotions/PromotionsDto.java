package ru.sparural.engine.api.dto.promotions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromotionsDto {
    Integer id;
    Integer priority;
    String title;
    String description;
    String shortDescription;
    Date begin;
    String end;
    String rewardThumbnail;
    String rewardImageId;
    List<String> brandIds;
    List<Brand> brands;
    String brandId;
    List<Image> images;
    List<Object> instructions;
    Integer merchantsCount;
}
