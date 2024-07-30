package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.AllowNullButNotEmpty;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class AllowNullButNotEmptyValidator implements ConstraintValidator<AllowNullButNotEmpty, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        return !value.isBlank();
    }
}
