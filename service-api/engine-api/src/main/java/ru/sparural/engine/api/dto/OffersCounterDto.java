package ru.sparural.engine.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.ValidateOnboxBanner;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OffersCounterDto {
    Long    id;
    String  loymaxOfferId;
    @NotNull(message = "Please enter Loymax Counter ID")
    String  loymaxCounterId;
    Integer maxValue;
    Boolean isPublic;
    Long    offerId;
}
