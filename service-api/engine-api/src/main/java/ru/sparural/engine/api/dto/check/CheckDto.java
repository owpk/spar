package ru.sparural.engine.api.dto.check;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.Currency;
import ru.sparural.engine.api.dto.merchant.Merchants;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckDto {
    Long id;
    Long cardId;
    Long dateTime;
    Boolean isRefund;
    Long checkNumber;
    Double amount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Currency currency;
    @JsonIgnore
    Long merchantsId;
    @JsonIgnore
    Long currenciesId;
    @JsonIgnore
    String externalPurchaseId;
    @JsonIgnore
    Long userId;
    Merchants merchant;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Item> items;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Withdraw> withdraws;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<Reward> rewards;
}