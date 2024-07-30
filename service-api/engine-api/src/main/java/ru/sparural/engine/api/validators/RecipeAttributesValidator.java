package ru.sparural.engine.api.validators;

import ru.sparural.engine.api.dto.recipes.RecipeAttributesDto;
import ru.sparural.engine.api.validators.annotations.ValidateRecipeAttribute;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Vorobyev Vyacheslav
 */
public class RecipeAttributesValidator extends AbsDtoValidator<ValidateRecipeAttribute, RecipeAttributesDto> {

    @Override
    public boolean isValid(RecipeAttributesDto value, ConstraintValidatorContext context) {
        if (value.getDraft() != null) {
            var draft = value.getDraft();
            if (!draft) {
                var name = value.getName();
                if (checkForNullOr(name, String::isBlank)) {
                    setCustomMessage("When not draft, name is required", context);
                    return false;
                }
            }
        }
        return true;
    }
}
