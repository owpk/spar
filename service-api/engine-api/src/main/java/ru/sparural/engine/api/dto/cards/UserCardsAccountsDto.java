package ru.sparural.engine.api.dto.cards;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.account.AccountsDto;
import ru.sparural.engine.api.dto.user.account.UserAccounts;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCardsAccountsDto {
    List<UserCardDto> myCards;
    List<UserAccounts> accounts;
}