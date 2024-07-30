package ru.sparural.engine.api.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class PersonalOfferUserDto {
    Long id;
    Long userId;
    Long personalOfferId;
    String data;
}
