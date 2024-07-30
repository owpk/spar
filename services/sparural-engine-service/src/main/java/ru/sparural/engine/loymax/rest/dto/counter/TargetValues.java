package ru.sparural.engine.loymax.rest.dto.counter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class TargetValues {
    String eventDateTime;
    String sourceDateTime;
    Long personId;
    String cardId;
    String partnerId;
    String deviceId;
    Long merchantId;
    String oAuthIdentifierInfo;
    String oAuthActivityType;
    String oAuthObjectId;
    String oAuthGroupId;
    String accountGroup;
    String refundedPurchaseId;
    String referralId;
    String purchaseId;
    String legalId;
    String eventDay;
    String eventMonth;
    String eventYear;
}
