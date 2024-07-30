package ru.sparural.rest.api.validators;

import ru.sparural.rest.api.trigger.MessageTemplateRequestForUpdateRestDto;
import ru.sparural.rest.api.validators.annotations.ValidateMessageTemplateUpdate;

import javax.validation.ConstraintValidatorContext;

public class MessageTemplateForUpdateValidator extends AbsDtoValidator<ValidateMessageTemplateUpdate, MessageTemplateRequestForUpdateRestDto> {

    @Override
    public boolean isValid(MessageTemplateRequestForUpdateRestDto value, ConstraintValidatorContext context) {
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
