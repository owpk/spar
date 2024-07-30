package ru.sparural.engine.api.dto.screen.mycards;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.check.CheckDto;
import ru.sparural.engine.api.dto.main.CategoryDto;
import ru.sparural.engine.api.dto.user.account.UserAccounts;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyCardsInfoScreenDto {
    List<UserCardDto> myCards;
    List<UserAccounts> accounts;
    Status status;
    List<CategoryDto> favoriteCategories;
    List<CheckDto> checks;
}
