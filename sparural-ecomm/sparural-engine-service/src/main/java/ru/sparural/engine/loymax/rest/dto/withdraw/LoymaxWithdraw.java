package ru.sparural.engine.loymax.rest.dto.withdraw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoymaxWithdraw {
    String withdrawType;
    String description;
    List<LoymaxWithdrawPositionInfo> positionInfo;
    LoymaxWithdrawAmount amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoymaxWithdraw that = (LoymaxWithdraw) o;
        return Objects.equals(withdrawType, that.withdrawType) && Objects.equals(description, that.description) && Objects.equals(positionInfo, that.positionInfo) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(withdrawType, description, positionInfo, amount);
    }
}
