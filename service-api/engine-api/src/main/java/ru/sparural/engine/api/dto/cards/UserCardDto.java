package ru.sparural.engine.api.dto.cards;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCardDto {
    Long id;
    String state;
    String number;
    String barCode;
    Boolean block;
    Long expiryDate;
    Boolean imOwner;
    String cardType;
}