package ru.sparural.engine.api.dto.user;

import java.util.List;
import lombok.Data;
import ru.sparural.engine.api.enums.UserFilterRegistrationTypes;

@Data
public class UserFilterDto {
    private List<Long> userIds;
    private List<Long> groupIds;
    private UserFilterRegistrationTypes registrationType;
}
