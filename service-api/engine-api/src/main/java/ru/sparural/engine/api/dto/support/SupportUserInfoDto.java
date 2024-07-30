package ru.sparural.engine.api.dto.support;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.UserDeviceDto;
import ru.sparural.engine.api.dto.cards.UserCardDto;
import ru.sparural.engine.api.dto.main.CardDto;
import ru.sparural.engine.api.dto.user.UserDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportUserInfoDto {
    UserDto user;
    List<UserCardDto> card;
    UserDeviceDto device;
}
