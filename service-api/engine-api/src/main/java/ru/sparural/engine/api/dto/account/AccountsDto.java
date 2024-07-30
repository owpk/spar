package ru.sparural.engine.api.dto.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.AccountsLifeTimesByPeriodDto;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.api.dto.Currency;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountsDto {
    @JsonProperty
    Long id;
    Double amount;
    @JsonIgnore
    Long userId;
    Currency currency;
    Double notActivatedAmount;
    List<AccountsLifeTimesByTimeDTO> accountsLifeTimesByTime;
    List<AccountsLifeTimesByPeriodDto> accountsLifeTimesByPeriod;
}
