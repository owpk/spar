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
public class Withdraw {
    Long id;
    String withdrawType;
    String description;
    Double amount;
    Currency currency;
    @JsonIgnore
    Long checkId;
    @JsonIgnore
    Long currenciesId;
}
