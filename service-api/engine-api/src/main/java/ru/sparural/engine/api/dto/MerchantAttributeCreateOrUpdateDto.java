package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.ValidateMerchantAttribute;

import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidateMerchantAttribute
public class MerchantAttributeCreateOrUpdateDto {
    @Size(max = 100, message = "Max length of name is 100 characters")
    String name;
    @Builder.Default
    Boolean draft = true;
}
