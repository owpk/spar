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
public class LoymaxLifeTimesByTime {
    Long amount;
    // "2018-06-08T06:54:33.806Z" <!--Дата активации/сгорания бонусов.-->
    String date;
}