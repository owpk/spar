package ru.sparural.engine.api.dto.support;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.user.UserDto;

@Getter
@Setter
@ToString
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupportChatFullMessageDto {
    @JsonUnwrapped
    SupportChatMessageDto unwrappedFields;
    UserDto sender;
}
