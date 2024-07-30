package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountsLifeTimesByPeriodDto {
    @JsonProperty
    Long id;
    @JsonIgnore
    Long accountId;
    Integer activationAmount;
    Integer expirationAmount;
    String period;
}
