package ru.sparural.engine.api.dto.templates;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.NotificationsTypesDto;
import ru.sparural.engine.api.dto.ScreenDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplateDto {
    Long id;
    String messageType;
    String name;
    String subject;
    String message;
    String messageHTML;
    ScreenDto screen;
    NotificationsTypesDto notificationType;
    Boolean sendToEveryone;
    List<MessageTemplatesUserDto> users;
    List<MessageTemplateUsersGroupDto> usersGroup;
    Boolean isSystem;
    Boolean requred;
    Integer lifetime;
    FileDto photo;
}
