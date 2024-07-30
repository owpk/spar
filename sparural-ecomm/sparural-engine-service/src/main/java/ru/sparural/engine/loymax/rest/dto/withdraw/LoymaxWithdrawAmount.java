package ru.sparural.engine.loymax.rest.dto.withdraw;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxCurrency;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoymaxWithdrawAmount that = (LoymaxWithdrawAmount) o;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency) && Objects.equals(currencyInfo, that.currencyInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency, currencyInfo);
    }
}

