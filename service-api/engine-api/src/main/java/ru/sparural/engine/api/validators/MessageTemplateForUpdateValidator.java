package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.templates.MessageTemplateRequestForUpdateDto;
import ru.sparural.engine.api.validators.annotations.ValidateMessageTemplateUpdate;

import javax.validation.ConstraintValidatorContext;

public class MessageTemplateForUpdateValidator extends AbsDtoValidator<ValidateMessageTemplateUpdate, MessageTemplateRequestForUpdateDto> {

    @Override
    public boolean isValid(MessageTemplateRequestForUpdateDto value, ConstraintValidatorContext context) {
        String messageType = value.getMessageType();
        if (messageType.equals("email")) {
            var bool = !checkForNullField(value.getSubject());
            if (checkForNullField(value.getMessage()) || value.getMessage().isBlank()) {
                bool = checkForNullField(value.getMessageHTML());
            } else if (!checkForNullField(value.getMessageHTML())) {
                bool = value.getMessageHTML().length() <= 10000;
            }
            return bool;
        }
        if (messageType.equals("push")
                || messageType.equals("viber")
                || messageType.equals("whatsapp")) {
            return !checkForNullField(value.getMessage()) && value.getMessage().length() <= 255;
        }
        if (messageType.equals("sms")) {
            return !checkForNullField(value.getMessage()) && value.getMessage().length() <= 100;
        }

        return true;
    }
}
