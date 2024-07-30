package ru.sparural.engine.loymax.rest.dto.withdraw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
}
