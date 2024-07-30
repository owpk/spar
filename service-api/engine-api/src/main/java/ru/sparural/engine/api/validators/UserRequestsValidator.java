package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.UserRequestsDto;
import ru.sparural.engine.api.validators.annotations.ValidateUserRequests;

import javax.validation.ConstraintValidatorContext;

public class UserRequestsValidator extends AbsDtoValidator<ValidateUserRequests, UserRequestsDto> {
    @Override
    public boolean isValid(UserRequestsDto value, ConstraintValidatorContext context) {
        var draft = value.getDraft();

        if (!draft) {
            if (checkForNullField(value.getFullName())) {
                setCustomMessage("When not draft, name is required, please your name", context);
                return false;
            }
            if (checkForNullOr(value.getFullName(), String::isBlank)) {
                setCustomMessage("When not draft, name is required, name must not be empty", context);
                return false;
            }
            if (checkForNullField(value.getEmail())) {
                setCustomMessage("When not draft, email is required, please write email", context);
                return false;
            }
            if (checkForNullOr(value.getEmail(), String::isBlank)) {
                setCustomMessage("When not draft, name is required, email must not be empty", context);
                return false;
            }
            if (value.getSubjectId() == null) {
                setCustomMessage("When not draft, id topic is required, please select topic", context);
                return false;
            }
            if (checkForNullField(value.getMessage())) {
                setCustomMessage("When not draft, text of message is required, please write message", context);
                return false;
            }
            if (checkForNullOr(value.getMessage(), String::isBlank)) {
                setCustomMessage("When not draft, name is required, message must not be empty", context);
                return false;
            }
        }
        return true;
    }
}
