package ru.sparural.engine.loymax.rest.dto.cards;

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
public class LoymaxCardAttachStatusDto {
    String cardNumber;
    Boolean isStarted;
    Long maxCountOfCards;
    Long currentCountOfAttachedCards;
    Long maximumPersonCountForGroup;
    Long currentPersonCountInGroup;
}
