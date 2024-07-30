package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.MerchantCommentsDto;
import ru.sparural.engine.api.validators.annotations.ValidateMerchantComments;

import javax.validation.ConstraintValidatorContext;

public class MerchantCommentsValidator extends AbsDtoValidator<ValidateMerchantComments, MerchantCommentsDto> {

    @Override
    public boolean isValid(MerchantCommentsDto value, ConstraintValidatorContext context) {
        var grade = value.getGrade();
        if (grade < 5) {
            return !checkForNullField(value.getQuestions()) && !value.getQuestions().isEmpty();
        }
        return true;
    }
}
