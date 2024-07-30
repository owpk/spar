package ru.sparural.engine.loymax.rest.dto.currency;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class LoymaxAmount {
    Double amount;
    String currency;
    LoymaxCurrency currencyInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoymaxAmount that = (LoymaxAmount) o;
        return Objects.equals(amount, that.amount) && Objects.equals(currency, that.currency) && Objects.equals(currencyInfo, that.currencyInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency, currencyInfo);
    }
}
