package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.ValidateCitySelect;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class CitySelectValidator extends AbsDtoValidator<ValidateCitySelect, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (checkForNullField(value))
            return true;
        if (!value.matches("(All|Selection|Nowhere)")) {
            setCustomMessage("Available citySelect values (All|Selection|Nowhere)", context);
            return false;
        }
        return true;
    }
}
