package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.ValidateQuestionType;

import javax.validation.ConstraintValidatorContext;


public class QuestionTypeValidator extends AbsDtoValidator<ValidateQuestionType, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (checkForNullField(value))
            return true;
        if (!value.matches("(NoAnswer|MultipleChoice)")) {
            setCustomMessage("Available question type values (NoAnswer|MultipleChoice)", context);
            return false;
        }
        return true;
    }
}
