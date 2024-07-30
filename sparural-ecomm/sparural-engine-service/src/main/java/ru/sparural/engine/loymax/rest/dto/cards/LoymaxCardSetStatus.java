package ru.sparural.engine.loymax.rest.dto.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxCardSetStatus {
    Long maxCountOfMainCardsForUser;
    Long currentCountOfMainCards;
    Boolean isCardSetAllowed;
}
