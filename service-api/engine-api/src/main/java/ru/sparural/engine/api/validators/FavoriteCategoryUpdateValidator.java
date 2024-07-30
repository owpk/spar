package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.validators.annotations.ValidateFavoriteCategoryUpdate;

import javax.validation.ConstraintValidatorContext;

public class FavoriteCategoryUpdateValidator extends AbsDtoValidator<ValidateFavoriteCategoryUpdate, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        //TODO: check validation
        if (!value.getClass().getSimpleName().equals("Boolean")) {
            setCustomMessage("isPublic takes a boolean value ", context);
            return false;
        }
        return true;
    }
}
