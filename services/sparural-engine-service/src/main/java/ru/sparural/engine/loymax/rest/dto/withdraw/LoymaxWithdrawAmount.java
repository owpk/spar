package ru.sparural.engine.loymax.rest.dto.withdraw;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class LoymaxWithdrawAmount {
    Double amount;
    String currency;
    LoymaxCurrency currencyInfo;
}

