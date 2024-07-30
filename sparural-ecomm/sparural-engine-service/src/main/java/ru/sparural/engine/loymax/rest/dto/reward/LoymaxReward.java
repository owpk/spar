package ru.sparural.engine.loymax.rest.dto.reward;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxAmount;

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
public class LoymaxReward {
    String offerExternalId;
    String rewardType;
    String description;
    LoymaxAmount amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoymaxReward that = (LoymaxReward) o;
        return Objects.equals(offerExternalId, that.offerExternalId) && Objects.equals(rewardType, that.rewardType) && Objects.equals(description, that.description) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offerExternalId, rewardType, description, amount);
    }
}
