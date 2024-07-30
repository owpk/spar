package ru.sparural.engine.loymax.rest.dto.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxUserCardDto {
    Long id;
    String state;
    String number;
    String barCode;
    Boolean block;
    Long expiryDate;
    @JsonProperty
    LoymaxUserCardOwnerInfo cardOwnerInfo;
    LoymaxUserCardCategory cardCategory;
}