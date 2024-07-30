package ru.sparural.engine.api.dto.templates;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.validators.annotations.ValidateMessageTemplateUpdate;

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
public class MessageTemplateRequestForUpdateDto {
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
    Long screenId;
    Long notificationTypeId;
    Boolean sendToEveryone;
    List<Long> users;
    List<Long> usersGroup;
    Boolean requred = false;
    @NotNull
    TriggerRequestDto trigger;
    @NotNull(message = "Please specify lifetime of message")
    @Min(value = 0, message = "Lifetime must be positive number")
    Integer lifetime;
    FileDto photo;
}
