package ru.sparural.engine.api.dto.user.account;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.AccountsLifeTimesByTimeDTO;
import ru.sparural.engine.api.dto.Currency;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserAccounts {
    Long id;
    Double amount;
    Currency currency;
    List<AccountsLifeTimesByTimeDTO> accountLifeTimesByTime;
}
