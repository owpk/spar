package ru.sparural.engine.api.dto.main;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CardDto {
    Long id;
    Long number;
    Long barCode;
    Boolean block;
    Long expiryDate;
    String status;
    Boolean iAmOwner;
}
