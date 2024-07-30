package ru.sparural.engine.loymax.rest.dto.account;

import lombok.*;
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
public class LoymaxUserBalanceInfoItemsDto {
    List<LoymaxUserBalanceInfoDto> items;
}