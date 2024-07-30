package ru.sparural.engine.loymax.rest.dto.offer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.LoymaxImage;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxOffer {

    @JsonProperty(value = "$type")
    String type;
    Attribute attribute;
    Boolean canSelectGoods;
    Long id;
    Integer priority;
    String title;
    String description;
    String shortDescription;
    String begin;
    String end;
    String rewardImageId;
    List<String> brandIds;
    List<Brand> brands;
    String brandId;
    List<LoymaxImage> images;
    List<String> instructions;
    Integer merchantsCount;
}
