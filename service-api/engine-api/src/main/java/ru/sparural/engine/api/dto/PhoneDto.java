package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.validators.annotations.Phone;

/**
 * @author Vorobyev Vyacheslav
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhoneDto {

    @Phone
    String phoneNumber;
}
