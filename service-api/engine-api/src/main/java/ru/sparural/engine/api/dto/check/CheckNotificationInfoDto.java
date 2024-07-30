package ru.sparural.engine.api.dto.check;

import lombok.Data;
import ru.sparural.engine.api.dto.user.UserNotificationInfoDto;

@Data
public class CheckNotificationInfoDto {
    private CheckDto check;
    private UserNotificationInfoDto userNotificationInfo;
}
