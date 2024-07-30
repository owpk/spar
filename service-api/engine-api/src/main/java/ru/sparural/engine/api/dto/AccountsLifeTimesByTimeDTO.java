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
public class AccountsLifeTimesByTimeDTO {
    @JsonProperty
    Long id;
    @JsonIgnore
    Long accountId;
    Long amount;
    Long date;
}
