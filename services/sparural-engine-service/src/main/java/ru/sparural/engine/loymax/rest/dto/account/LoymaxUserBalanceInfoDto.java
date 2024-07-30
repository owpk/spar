package ru.sparural.engine.loymax.rest.dto.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoymaxUserBalanceInfoDto {
    Double amount;
    Boolean accountIsBlocked;
    Double notActivatedAmount;
    LoymaxCurrency currency;
    List<LoymaxLifeTimesByTime> lifeTimesByTime;
    List<LoymaxLifeTimeByPeriod> lifeTimesByPeriod;
}