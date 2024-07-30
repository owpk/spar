package ru.sparural.engine.api.dto.user;

import java.util.List;
import lombok.Data;

@Data
public class UserNotificationInfoDto {
    private UserDto user;
    private List<UserPushTokenDto> tokens;
}
