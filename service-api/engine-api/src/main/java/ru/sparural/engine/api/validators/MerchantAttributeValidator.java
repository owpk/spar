package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.MerchantAttributeCreateOrUpdateDto;
import ru.sparural.engine.api.validators.annotations.ValidateMerchantAttribute;

import javax.validation.ConstraintValidatorContext;

public class MerchantAttributeValidator extends AbsDtoValidator<ValidateMerchantAttribute, MerchantAttributeCreateOrUpdateDto> {

    @Override
    public boolean isValid(MerchantAttributeCreateOrUpdateDto value, ConstraintValidatorContext context) {
        if (!value.getDraft()) {
            return !value.getName().isBlank();
        }
        return true;
    }

}