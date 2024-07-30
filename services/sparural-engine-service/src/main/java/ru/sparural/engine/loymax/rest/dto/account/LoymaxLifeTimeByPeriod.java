package ru.sparural.engine.loymax.rest.dto.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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