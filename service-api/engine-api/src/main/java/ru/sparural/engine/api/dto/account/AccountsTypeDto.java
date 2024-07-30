package ru.sparural.engine.api.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.Currency;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountsTypeDto {
    Long id;
    String name;
    Currency currency;
    @JsonIgnore
    Long currenciesId;
    Integer order;
}
