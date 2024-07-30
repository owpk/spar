package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.Phone;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class PhoneNumberValidator extends AbsDtoValidator<Phone, String> {

    public static final String regexp = "^7\\d{10}$";

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (checkForNullField(value)) return true;
        return value.matches(regexp);
    }
}
