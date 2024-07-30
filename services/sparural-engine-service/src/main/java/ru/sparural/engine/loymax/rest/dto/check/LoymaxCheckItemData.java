package ru.sparural.engine.loymax.rest.dto.check;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.loymax.rest.dto.currency.LoymaxAmount;
import ru.sparural.engine.loymax.rest.dto.reward.LoymaxReward;
import ru.sparural.engine.loymax.rest.dto.withdraw.LoymaxWithdraw;

import java.util.List;

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
public class LoymaxCheckItemData {
    String offerExternalId;
    String externalPurchaseId;
    Long chequeNumber;
    Boolean isRefund;
    LoymaxAmount amount;
    List<LoymaxReward> rewards;
    List<LoymaxCheckItemPosition> chequeItems;
    List<LoymaxWithdraw> withdraws;
}
