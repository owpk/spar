package ru.sparural.engine.loymax.rest.dto.account;

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
public class LoymaxLifeTimeByPeriod {
    Integer activationAmount;
    Integer expirationAmount;
    String period;
}