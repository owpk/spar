package ru.sparural.rest.api.trigger;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.rest.api.ScreenRestDto;
import ru.sparural.rest.api.file.FileRestDto;
import ru.sparural.rest.api.notifications.NotificationsTypesRestDto;
import ru.sparural.rest.api.validators.annotations.ValidateMessageTemplateUpdate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidateMessageTemplateUpdate
public class MessageTemplateRequestForUpdateRestDto {
    @NotNull(message = "Please specify type of message")
    @Pattern(regexp = "push|sms|email|viber|whatsapp",
            message = "Incorrect value. The type of message must be push, sms, email, viber or whatsapp")
    String messageType;
    @Size(max = 255, message = "Max length of name is 255 characters")
    String name;
    @Size(max = 255, message = "Max length of subject is 255 characters")
    String subject;
    String message;
    String messageHTML;
    ScreenRestDto screen;
    NotificationsTypesRestDto notificationType;
    Boolean sendToEveryone;
    List<Long> users;
    List<Long> usersGroup;
    Boolean requred = false;
    @NotNull
    TriggerRequestRestDto trigger;
    @NotNull(message = "Please specify lifetime of message")
    @Min(value = 0, message = "Lifetime must be positive number")
    Integer lifetime;
    FileRestDto photo;

    Long currencyId;
    Integer currencyDaysBeforeBurning;
    Integer daysWithoutPurchasing;
}
