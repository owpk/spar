package ru.sparural.engine.api.dto.check;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.Currency;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Reward {
    Long id;
    String rewardType;
    String description;
    Double amount;
    Currency currency;
    @JsonIgnore
    Long currenciesId;
    @JsonIgnore
    Long checkId;
}
