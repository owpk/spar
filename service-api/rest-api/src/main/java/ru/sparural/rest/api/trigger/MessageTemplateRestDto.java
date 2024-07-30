package ru.sparural.rest.api.trigger;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.rest.api.ScreenRestDto;
import ru.sparural.rest.api.file.FileRestDto;
import ru.sparural.rest.api.notifications.NotificationsTypesRestDto;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageTemplateRestDto {
    Long id;
    String messageType;
    String name;
    String subject;
    String message;
    String messageHTML;
    ScreenRestDto screen;
    NotificationsTypesRestDto notificationType;
    Boolean sendToEveryone;
    List<MessageTemplatesUserRestDto> users;
    List<MessageTemplateUsersGroupRestRestDto> usersGroup;
    Boolean isSystem;
    Boolean requred;
    Integer lifetime;
    TriggerRestDto trigger;
    FileRestDto photo;

    Long currencyId;
    Integer currencyDaysBeforeBurning;
    Integer daysWithoutPurchasing;
}
